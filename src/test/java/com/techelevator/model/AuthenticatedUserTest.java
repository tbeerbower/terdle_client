package com.techelevator.model;

import static org.junit.Assert.*;

public class AuthenticatedUserTest {

    @org.junit.Test
    public void getToken() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setToken("token");
        assertEquals("token", authenticatedUser.getToken());
    }

    @org.junit.Test
    public void setToken() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setToken("token");
        assertEquals("token", authenticatedUser.getToken());
    }

    @org.junit.Test
    public void getUser() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        User user = new User();
        authenticatedUser.setUser(user);
        assertEquals(user, authenticatedUser.getUser());
    }

    @org.junit.Test
    public void setUser() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        User user = new User();
        authenticatedUser.setUser(user);
        assertEquals(user, authenticatedUser.getUser());
    }
}