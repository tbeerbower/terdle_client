package com.techelevator.model;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void getGameId() {
        Game game = new Game(1, "word", null, Game.Type.DAILY);
        assertEquals(1, game.getGameId());
    }

    @Test
    public void getWord() {
        Game game = new Game(1, "word", null, Game.Type.DAILY);
        assertEquals("word", game.getWord());
    }

    @Test
    public void getDate() {
        LocalDate date = LocalDate.now();
        Game game = new Game(1, "word", date, Game.Type.DAILY);
        assertEquals(date, game.getDate());
    }

    @Test
    public void getType() {
        Game game = new Game(1, "word", null, Game.Type.DAILY);
        assertEquals(Game.Type.DAILY, game.getType());
    }
}