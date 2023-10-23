package com.techelevator.model;

import java.util.ArrayList;
import java.util.List;

public class UserGame extends Game {

    public enum Match {
        EXACT_MATCH,
        WRONG_LOCATION,
        NO_MATCH
    }

    public static class MatchPair {
        private final Match match;
        private final char c;

        public MatchPair(Match match, char c) {
            this.match = match;
            this.c = c;
        }

        public Match getMatch() {
            return match;
        }

        public char getChar() {
            return c;
        }
    }

    private int userId;
    private final List<String> guesses = new ArrayList<>();
    private boolean success;

    public UserGame() {
    }

    public UserGame(int userId, Type type) {
        super(0, null, null, type);
        this.userId = userId;
        this.success = false;
    }

    public UserGame(int userId, Game game) {
        super(game.getGameId(), game.getWord(), game.getDate(), game.getType());
        this.userId = userId;
        this.success = false;
    }

    public int getUserId() {
        return userId;
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public boolean isSuccess() {
        return success;
    }
}
