package com.techelevator.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class AuthorityTest {

    @Test
    public void getName() {
        Authority authority = new Authority();
        authority.setName("name");
        assertEquals("name", authority.getName());
    }

    @Test
    public void setName() {
        Authority authority = new Authority();
        authority.setName("name");
        assertEquals("name", authority.getName());
    }

    @Test
    public void testEquals() {
        Authority authority = new Authority();
        authority.setName("name");
        Authority authority2 = new Authority();
        authority2.setName("name");
        assertEquals(authority, authority2);
    }

    @Test
    public void testHashCode() {
        Authority authority = new Authority();
        authority.setName("name");
        Authority authority2 = new Authority();
        authority2.setName("name");
        assertEquals(authority.hashCode(), authority2.hashCode());
    }
}