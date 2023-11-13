package com.techelevator.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserCredentialsTest {

    @Test
    public void getUsername() {
        UserCredentials userCredentials = new UserCredentials("username", "password");
        assertEquals("username", userCredentials.getUsername());
    }

    @Test
    public void getPassword() {
        UserCredentials userCredentials = new UserCredentials("username", "password");
        assertEquals("password", userCredentials.getPassword());
    }
}