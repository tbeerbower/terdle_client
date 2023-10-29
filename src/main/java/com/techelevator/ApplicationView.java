package com.techelevator;

import com.techelevator.model.Game;
import com.techelevator.model.UserCredentials;
import com.techelevator.model.UserGame;
import com.techelevator.utils.BasicConsole;
import com.techelevator.utils.TextEffect;
import com.techelevator.utils.TextGrid;

import java.util.List;
import java.util.Map;

/**
 * AdminView is used for gathering information from the user and presenting information to the user.
 *
 * It depends on an object that implements the BasicConsole interface to handle reading from and writing to the console.
 */
public class ApplicationView {
    public static final int VERTICAL_CELL_PADDING = 1;
    public static final int HORIZONTAL_CELL_PADDING = 2;

    private static final TextEffect ERROR_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_RED, TextEffect.Code.BLACK);
    private static final TextEffect SUCCESS_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_GREEN);
    private static final TextEffect NO_MATCH_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_WHITE, TextEffect.Code.BLACK);
    private static final TextEffect WRONG_LOCATION_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_YELLOW, TextEffect.Code.BLACK);
    private static final TextEffect EXACT_MATCH_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_GREEN);

    private static final Map<UserGame.Match, TextEffect> MATCH_COLORS = Map.of(
        UserGame.Match.NO_MATCH,       NO_MATCH_COLORS,
        UserGame.Match.WRONG_LOCATION, WRONG_LOCATION_COLORS,
        UserGame.Match.EXACT_MATCH,    EXACT_MATCH_COLORS
    );

    private final BasicConsole console;

    // Constructor uses dependency injection to get the console object to use for printing.
    public ApplicationView(BasicConsole console) {
        this.console = console;
    }

    /**
     * Adds a blank line to the display.
     */
    public void displayBlankLine() {
        console.printBlankLine();
    }

    /**
     * Adds a message to the display in normal text.
     * @param message the message to show
     */
    public void displayMessage(String message) {
        console.printMessage(message);
    }

    /**
     * Adds an error message to the display in red text.
     * @param message the message to show
     */
    public void displayErrorMessage(String message) {
        console.printErrorMessage(ERROR_COLORS.apply(message));
        console.printBlankLine();
    }

    /**
     * Adds an error message to the display in green text.
     * @param message the message to show
     */
    public void displaySuccessMessage(String message) {
        console.printErrorMessage(SUCCESS_COLORS.apply(message));
        console.printBlankLine();
    }

    /**
     * Displays a welcome message with a green banner.
     */
    public void displayWelcomeMessage() {
        String message = "Welcome to TErdle!";
        console.printBanner(SUCCESS_COLORS, message);
    }

    public void displayUserGameMatches(List<UserGame.MatchPair[]> matchesList) {
        TextGrid.Builder builder = new TextGrid.Builder(Game.WORD_LENGTH, false).
                setVerticalCellPadding(VERTICAL_CELL_PADDING).setHorizontalCellPadding(HORIZONTAL_CELL_PADDING);

        for (UserGame.MatchPair[] matches : matchesList) {
            for (int charIndex = 0; charIndex < Game.WORD_LENGTH; ++charIndex) {
                TextEffect colors = MATCH_COLORS.get(matches[charIndex].getMatch());
                String guessChar = String.valueOf(matches[charIndex].getChar()).toUpperCase();
                builder = builder.addCell(colors, guessChar);
            }
        }
        displayMessage(builder.generate().toString());
    }

    /**
     * Prompts for the values required to log-in - username & password
     * @return a UserCredentials object
     */
    public UserCredentials promptForCredentials() {
        console.printMessage("Please login.");
        String username = console.promptForString("Username: ");
        String password = console.promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForGuess(int guessNumber) {
        return console.promptForString(String.format("Enter guess number %d: ", guessNumber)).toLowerCase();
    }
}
