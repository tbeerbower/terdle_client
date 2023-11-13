package com.techelevator.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserGameTest {

    @Test
    public void getUserId() {
        UserGame userGame = new UserGame(1, Game.Type.DAILY);
        assertEquals(1, userGame.getUserId());
    }

    @Test
    public void getGuesses() {
        UserGame userGame = new UserGame(1, Game.Type.DAILY);
        userGame.getGuesses().add("about");
        assertEquals(1, userGame.getGuesses().size());
        assertTrue(userGame.getGuesses().contains("about"));
        userGame.getGuesses().add("train");
        assertEquals(2, userGame.getGuesses().size());
        assertTrue(userGame.getGuesses().contains("train"));
    }

    @Test
    public void isSuccess() {
        UserGame userGame = new UserGame(1, Game.Type.DAILY);
        assertFalse(userGame.isSuccess());
    }
}