package com.techelevator;

import com.techelevator.model.*;
import com.techelevator.services.AuthenticationService;
import com.techelevator.services.GameService;
import com.techelevator.utils.*;

import java.util.List;

/**
 * AdminController controls the application flow and manages all of its operations through a series of menus.
 * It depends on other classes for:
 * - interacting with the user - AdminView
 * - interacting with the server REST API - AuthenticationService, GameService
 */

public class ApplicationController {

    private static final TextEffect STAT_HEADER_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_WHITE, TextEffect.Code.BLACK, TextEffect.Code.BOLD, TextEffect.Code.ITALIC);
    private static final TextEffect STAT_SUCCESS_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_GREEN, TextEffect.Code.BLACK);
    private static final TextEffect STAT_SUMMARY_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_PURPLE, TextEffect.Code.BLACK);

    // Menu related constants
    private static final String MAIN_MENU_NAME = "MainMenu";
    private static final String ADMIN_MAIN_MENU_NAME = "AdminMainMenu";
    private static final String ADMIN_MENU_NAME = "AdminMenu";
    private static final String LOGIN_MENU_NAME = "LoginMenu";

    private static final MenuSystem.Menu<ApplicationController> MAIN_MENU =
            new MenuSystem.Builder<ApplicationController>().
            addItem("Play Daily Game", ApplicationController::playDailyGame).
            addItem("Play Random Game", ApplicationController::playRandomGame).
            addItem("Show Game Statistics", ApplicationController::showUserGameStats).
            addItem("Log Out", ApplicationController::logOut).
            getMenu(MAIN_MENU_NAME, "Main Menu");
    private static final MenuSystem.Menu<ApplicationController> ADMIN_MAIN_MENU =
            new MenuSystem.Builder<ApplicationController>().
            addItem("Play Daily Game", ApplicationController::playDailyGame).
            addItem("Play Random Game", ApplicationController::playRandomGame).
            addItem("Show Game Statistics", ApplicationController::showUserGameStats).
            addItem("Admin Menu", ApplicationController::gotoAdminMenu).
            addItem("Log Out", ApplicationController::logOut).
            getMenu(ADMIN_MAIN_MENU_NAME, "Main Menu");
    private static final MenuSystem.Menu<ApplicationController> ADMIN_MENU =
            new MenuSystem.Builder<ApplicationController>().
            addItem("Return to Main Menu", ApplicationController::exit).
            getMenu(ADMIN_MENU_NAME, "Admin Menu");
    private static final MenuSystem.Menu<ApplicationController> LOGIN_MENU =
            new MenuSystem.Builder<ApplicationController>().
            addItem("Login", ApplicationController::handleLogin).
            addItem("Exit", ApplicationController::exit).
            getMenu(LOGIN_MENU_NAME, "Login Menu");

    private static final List<MenuSystem.Menu<ApplicationController>> MENUS =
            List.of(LOGIN_MENU, MAIN_MENU, ADMIN_MAIN_MENU, ADMIN_MENU);

    // Service classes for communication to the REST API
    private final AuthenticationService authService;
    private final GameService gameService;

    // The view manages all the user interaction, inputs and outputs.
    private final ApplicationView view;

    // The currently logged-in user, or null if no login
    private AuthenticatedUser currentUser;

    private final MenuSystem<ApplicationController> menuSystem;

    /**
     * Constructor - creates instances of the view and service classes. Dependencies are passed in
     * from the main Application class.
     *
     * @param console    - a class that implements BasicConsole to pass to the AdminView
     * @param apiBaseUrl - the base url for communication with the server
     */
    public ApplicationController(BasicConsole console, String apiBaseUrl) {
        view = new ApplicationView(console);
        authService = new AuthenticationService(apiBaseUrl);
        gameService = new GameService(apiBaseUrl);
        menuSystem = new MenuSystem<>(this, console, MENUS, LOGIN_MENU_NAME);
    }

    /**
     * The run() method starts the program flow by displaying the main program menu,
     * and responding to the user's selection.
     */
    public void run() {
        try {
            view.displayWelcomeMessage();
            menuSystem.run();
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


    // ***** Helper Methods ***************************************************

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
            gameService.setAuthToken(currentUser.getToken());
            menuSystem.makeMenuCurrent(currentUser.getUser().isAdmin() ? ADMIN_MAIN_MENU_NAME : MAIN_MENU_NAME);
        }
    }

    private void playDailyGame() {
        UserGame userGame = null;
        int userId = currentUser.getUser().getId();
        Game game = gameService.getTodaysGame();
        if (game != null) {
            userGame = gameService.getUserGame(userId, game.getGameId());
        }
        if (userGame == null) {
            userGame = gameService.createUserGame(new UserGame(userId, Game.Type.DAILY));
        }
        playGame(userGame);
    }

    private void playRandomGame() {
        playGame(gameService.createUserGame(new UserGame(currentUser.getUser().getId(), Game.Type.RANDOM)));
    }

    private void playGame(UserGame userGame) {

        List<String> guesses = userGame.getGuesses();
        int guessNumber = guesses.size();

        if (guessNumber > 0) {
            view.displayBlankLine();
            view.displayMessage(String.format("You have already played this game and made %d %s.",
                    guessNumber, guessNumber > 1 ? "guesses" : "guess"));
            view.displayUserGameMatches(gameService.getGameMatches(userGame));
        }

        String word = userGame.getWord();
        while (!userGame.isSuccess() && guessNumber < Game.MAX_GUESSES) {
            String guessed = view.promptForGuess(guessNumber + 1);
            guesses.add(guessed);
            if (!gameService.updateGame(userGame)) {
                view.displayErrorMessage(String.format("%s is not a valid 5 letter word!", guessed));
            }
            int userId = userGame.getUserId();
            int gameId = userGame.getGameId();
            userGame = gameService.getUserGame(userId, gameId);
            guesses = userGame.getGuesses();
            guessNumber = guesses.size();
            view.displayUserGameMatches(gameService.getGameMatches(userGame));
        }
        if (userGame.isSuccess()) {
            view.displaySuccessMessage(String.format("You got it in %d %s!", guessNumber, guessNumber > 1 ? "tries" : "try"));
        } else {
            view.displayMessage(String.format("Sorry, you didn't get it.  The word you are looking for is %s.", word));
        }
    }

    private void showUserGameStats() {
        List<UserGame> games = gameService.getUserGames(currentUser.getUser().getId());
        int gamesStarted = games.size();
        int gamesCompleted = 0;
        int successfulGames = 0;
        int totalNumberOfGuesses = 0;

        TextGrid.Builder builder = new TextGrid.Builder(5).setMaxCellHeight(2).setMaxCellWidth(10).
                setHorizontalAlign(TextGrid.HorizontalAlign.CENTER).setVerticalAlignment(TextGrid.VerticalAlign.TOP).
                setHorizontalCellPadding(1).setVerticalCellPadding(0);

        // Header row
        builder.addCell(STAT_HEADER_COLORS, "Date").addCell(STAT_HEADER_COLORS, "Word").
                addCell(STAT_HEADER_COLORS, "Last Guess").addCell(STAT_HEADER_COLORS, "Guesses").
                addCell(STAT_HEADER_COLORS, "Type");

        for (UserGame userGame : games) {
            int numberOfGuesses = userGame.getGuesses().size();
            if (userGame.isSuccess() || numberOfGuesses == 6) {
                gamesCompleted++;
                totalNumberOfGuesses += numberOfGuesses;
            }
            if (userGame.isSuccess()) {
                successfulGames++;
            }

            String lastGuess = userGame.getGuesses().isEmpty() ? "" : userGame.getGuesses().get(numberOfGuesses - 1);
            TextEffect rowColors = userGame.isSuccess() ? STAT_SUCCESS_COLORS : null;

            // data row
            builder.addCell(rowColors, userGame.getDate().toString()).
                    addCell(rowColors, userGame.getWord()).
                    addCell(rowColors, lastGuess).
                    addCell(rowColors, String.valueOf(numberOfGuesses)).
                    addCell(rowColors, userGame.getType().name());
        }

        view.displayMessage(builder.generate().toString());

        builder = new TextGrid.Builder(2).
                setHorizontalAlign(TextGrid.HorizontalAlign.CENTER).setVerticalAlignment(TextGrid.VerticalAlign.TOP).
                setHorizontalCellPadding(1).setVerticalCellPadding(0);

        double avgGuesses = totalNumberOfGuesses / (double) gamesCompleted;
        double successPercentage = successfulGames / (double) gamesCompleted * 100.0;

        builder.addCell(STAT_HEADER_COLORS, "Games started").addCell(STAT_SUMMARY_COLORS, String.valueOf(gamesStarted)).
                addCell(STAT_HEADER_COLORS, "Games completed").addCell(STAT_SUMMARY_COLORS, String.valueOf(gamesCompleted)).
                addCell(STAT_HEADER_COLORS, "Games won").addCell(STAT_SUMMARY_COLORS, String.valueOf(successfulGames)).
                addCell(STAT_HEADER_COLORS, "Games won %").addCell(STAT_SUMMARY_COLORS, String.valueOf(successPercentage)).
                addCell(STAT_HEADER_COLORS, "Average Guesses").addCell(STAT_SUMMARY_COLORS, String.valueOf(avgGuesses));

        view.displayMessage(builder.generate().toString());
    }

    private void gotoAdminMenu() {
        menuSystem.makeMenuCurrent(ADMIN_MENU_NAME);
    }

    private void logOut() {
        currentUser = null;
        exit();
    }

    private void exit() {
        menuSystem.exitCurrentMenu();
    }
}
