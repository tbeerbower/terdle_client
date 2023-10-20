package com.techelevator;

import com.techelevator.model.*;
import com.techelevator.services.AuthenticationService;
import com.techelevator.services.GameService;
import com.techelevator.utils.BasicConsole;
import com.techelevator.utils.BasicLogger;

import java.util.List;

/**
 * AdminController controls the application flow and manages all of its operations through a series of menus.
 * It depends on other classes for:
 *   - interacting with the user - AdminView
 *   - interacting with the server REST API - AuthenticationService, GameService
 */

public class ApplicationController {

    public static final int MAX_GUESSES = 6;


    // Menu options
    private static final String GOTO_ADMIN_MENU = "Go to Admin Menu";
    private static final String START_DAILY_GAME = "Start Daily Game";
    private static final String RETURN_TO_MAIN_MENU = "Return to Main Menu";
    private static final String PLAY_DAILY_GAME = "Play Daily Game";
    private static final String PLAY_RANDOM_GAME = "Play Random Game";
    private static final String SHOW_GAME_STATS = "Show Game Statistics";
    private static final String LOG_OUT = "Log Out";
    private static final String EXIT = "Exit the program";
    private static final String[] MAIN_MENU_OPTIONS = {PLAY_DAILY_GAME, PLAY_RANDOM_GAME, SHOW_GAME_STATS, LOG_OUT, EXIT};
    private static final String[] MAIN_MENU_OPTIONS_FOR_ADMIN = {PLAY_DAILY_GAME, PLAY_RANDOM_GAME, SHOW_GAME_STATS, GOTO_ADMIN_MENU, LOG_OUT, EXIT};
    private static final String[] ADMIN_MENU_OPTIONS = {START_DAILY_GAME, RETURN_TO_MAIN_MENU};


    // Service classes for communication to the REST API
    private final AuthenticationService authService;
    private final GameService gameService;

    // The view manages all the user interaction, inputs and outputs.
    private final ApplicationView view;

    // The currently logged-in user, or null if no login
    private AuthenticatedUser currentUser;

    /**
     * Constructor - creates instances of the view and service classes. Dependencies are passed in
     * from the main Application class.
     * @param console - a class that implements BasicConsole to pass to the AdminView
     * @param apiBaseUrl - the base url for communication with the server
     */
    public ApplicationController(BasicConsole console, String apiBaseUrl) {
        view = new ApplicationView(console);
        authService = new AuthenticationService(apiBaseUrl);
        gameService = new GameService(apiBaseUrl);
    }

    /**
     * The run() method starts the program flow by displaying the main program menu,
     * and responding to the user's selection.
     */
    public void run() {
        try {
            view.displayWelcomeMessage();

            boolean finished = false;
            String menuTitle = "Main Menu";
            String[] menuOptions = MAIN_MENU_OPTIONS;
            // The main menu loop
            while (!finished) {
                if (currentUser == null) {
                    handleLogin();
                    menuOptions = currentUser.getUser().isAdmin() ? MAIN_MENU_OPTIONS_FOR_ADMIN : MAIN_MENU_OPTIONS;
                } else {
                    String mainMenuSelection = view.selectFromMenu(menuTitle, menuOptions);
                    switch (mainMenuSelection) {
                        // ***** Main Menu Options
                        case PLAY_DAILY_GAME:
                            playGame(Game.Type.DAILY);
                            break;
                        case PLAY_RANDOM_GAME:
                            playGame(Game.Type.RANDOM);
                            break;
                        case SHOW_GAME_STATS:
                            showUserGameStats();
                            break;
                        case GOTO_ADMIN_MENU:
                            menuTitle = "Admin Menu";
                            menuOptions = ADMIN_MENU_OPTIONS;
                            break;
                        case LOG_OUT:
                            currentUser = null;
                            break;
                        case EXIT:
                            // Set finished to true so the loop exits.
                            finished = true;
                            break;
                        // ***** Admin Menu Options
                        case START_DAILY_GAME:
                            getOrStartTodaysGame();
                            break;
                        case RETURN_TO_MAIN_MENU:
                            menuTitle = "Main Menu";
                            menuOptions = currentUser.getUser().isAdmin() ? MAIN_MENU_OPTIONS_FOR_ADMIN : MAIN_MENU_OPTIONS;
                            break;
                    }
                }
            }
        } catch (Exception e) {
            /*
             * Note: A catch for general Exceptions is used here as a best practice to prevent
             * unexpected Exceptions from halting the application and displaying a stack trace
             * and unfiltered technical error message to the application users.
             */
            view.displayErrorMessage("An unexpected error has occurred. See the log file for details.");
            BasicLogger.log(e);
        }
    }

    /**
     * This application requires both correct credentials (username & password) and the Admin role to
     * successfully log in and use the application.
     */
    private void handleLogin() {
        // Use the view to handle user interactions
        UserCredentials credentials = view.promptForCredentials();

        // Use the service to handle communication with the server
        currentUser = authService.login(credentials);

        // Check for successful login (user is not null) AND admin user role (also known as an authority)
        // Note that the view is also used to give feedback to the user
        if (currentUser == null) {
            view.displayErrorMessage("Login failed.");
        } else {
            view.displaySuccessMessage("Login successful.");
            String token = currentUser.getToken();
            gameService.setAuthToken(token);
        }
    }

    private void playGame(Game.Type type) {
        UserGame userGame = getOrStartUserGame(currentUser.getUser().getId(), type);

        if (userGame != null) {

            List<String> guesses = userGame.getGuesses();
            int guessNumber = guesses.size();

            if (guessNumber > 0) {
                view.displayMessage(String.format("You have already played the daily game and made %d %s.",
                        guessNumber, guessNumber > 1 ? "guesses" : "guess"));
                view.displayUserGame(userGame);
            }

            String word = userGame.getWord();
            while (!userGame.isSuccess() && guessNumber < MAX_GUESSES) {
                String guessed = view.promptForGuess(guessNumber + 1);
                guesses.add(guessed);
                if (!gameService.updateGame(userGame)) {
                    System.out.printf("%s is not a valid 5 letter word!\n", guessed);
                }
                int userId = userGame.getUserId();
                int gameId = userGame.getGameId();
                userGame = gameService.getUserGame(userId, gameId);
                guesses = userGame.getGuesses();
                guessNumber = guesses.size();
                view.displayUserGame(userGame);
            }
            if (userGame.isSuccess()) {
                view.displaySuccessMessage(String.format("You got it in %d %s!\n", guessNumber, guessNumber > 1 ? "tries" : "try"));
            } else {
                view.displayMessage(String.format("Sorry, you didn't get it.  The word you are looking for is %s.\n", word));
            }
        } else {
           view.displayErrorMessage("The game can not be found!");
        }
    }

    private UserGame getOrStartUserGame(int userId, Game.Type type) {

        if (type == Game.Type.DAILY) {

            Game game = gameService.getTodaysGame();
            if (game != null) {
                return gameService.getOrCreateUserGame(userId, game);
            }
            return null;
        }
        Game game = gameService.add(new Game(type));
        return gameService.createUserGame(new UserGame(userId, game));
    }

    private Game getOrStartTodaysGame() {
        Game game = gameService.getTodaysGame();
        if (game == null) {
            game = gameService.add(new Game(Game.Type.DAILY));
        }
        return game;
    }

    private void showUserGameStats() {
        List<UserGame> games = gameService.getUserGames(currentUser.getUser().getId());
        int gamesStarted = games.size();
        int gamesCompleted = 0;
        int successfulGames = 0;
        int totalNumberOfGuesses = 0;

        view.displayBlankLine();
        view.displayMessage("Game                   Last     Num. of");
        view.displayMessage("Date          Word     Guess    Guesses    Success    Type");
        view.displayMessage("==============================================================");
        for(UserGame userGame : games) {
            int numberOfGuesses = userGame.getGuesses().size();
            if (userGame.isSuccess() || numberOfGuesses == 6) {
                gamesCompleted++;
                totalNumberOfGuesses += numberOfGuesses;
            }
            if (userGame.isSuccess()) {
                successfulGames++;
            }

            String lastGuess = userGame.getGuesses().isEmpty() ? "     " : userGame.getGuesses().get(numberOfGuesses - 1);
            view.displayMessage(String.format("%s    %s    %s       %d         %c        %s",
                    userGame.getDate(), userGame.getWord(), lastGuess,  numberOfGuesses,
                    userGame.isSuccess() ? '*' : ' ', userGame.getType().name()));
        }

        double avgGuesses = totalNumberOfGuesses / (double) gamesCompleted;
        double successPercentage = successfulGames / (double) gamesCompleted * 100.0;
        view.displayMessage("==============================================================");
        view.displayMessage( String.format("Games started   : %d", gamesStarted));
        view.displayMessage( String.format("Games completed : %d", gamesCompleted));
        view.displayMessage( String.format("Games won       : %d", successfulGames));
        view.displayMessage( String.format("Games won %%     : %6.2f", successPercentage));
        view.displayMessage( String.format("Average guesses : %4.2f", avgGuesses));
        view.displayBlankLine();
        view.displayBlankLine();
    }
}
