package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class UserGame extends Game {

    public enum Match {
        EXACT_MATCH,
        WRONG_LOCATION,
        NO_MATCH;

        @JsonValue
        public String toValue() {
            return name();
        }
    }

    public static class MatchPair {
        private Match match;
        @JsonProperty("char")
        private char c;

        public MatchPair() {
        }

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
    private final List<UserGame.MatchPair[]> matches = new ArrayList<>();
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

    public List<UserGame.MatchPair[]> getMatches() {
    	return matches;
    }

    public boolean isSuccess() {
        return success;
    }
}
