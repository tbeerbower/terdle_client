package com.techelevator.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void getId() {
        User user = new User();
        user.setId(1);
        assertEquals(1, user.getId());
    }

    @Test
    public void setId() {
        User user = new User();
        user.setId(1);
        assertEquals(1, user.getId());
    }

    @Test
    public void getUsername() {
        User user = new User();
        user.setUsername("username");
        assertEquals("username", user.getUsername());
    }

    @Test
    public void setUsername() {
        User user = new User();
        user.setUsername("username");
        assertEquals("username", user.getUsername());
    }

    @Test
    public void getAuthorities() {
        User user = new User();
        Authority authority = new Authority();
        authority.setName("name");
        Authority[] authorities = new Authority[]{authority};
        user.setAuthorities(authorities);
        assertTrue(user.getAuthorities().contains(authority));
    }

    @Test
    public void setAuthorities() {
        User user = new User();
        Authority authority = new Authority();
        authority.setName("name");
        Authority[] authorities = new Authority[]{authority};
        user.setAuthorities(authorities);
        assertTrue(user.getAuthorities().contains(authority));
    }

    @Test
    public void isAdmin() {
        User user = new User();
        Authority authority = new Authority();
        authority.setName("ROLE_ADMIN");
        Authority[] authorities = new Authority[]{authority};
        user.setAuthorities(authorities);
        assertTrue(user.isAdmin());

        user = new User();
        authority = new Authority();
        authority.setName("ROLE_USER");
        authorities = new Authority[]{authority};
        user.setAuthorities(authorities);
        assertFalse(user.isAdmin());
    }

    @Test
    public void testEquals() {
        Authority authority = new Authority();
        authority.setName("ROLE_ADMIN");
        Authority[] authorities = new Authority[]{authority};

        User user = new User();
        user.setUsername("username");
        user.setAuthorities(authorities);
        User user2 = new User();
        user2.setUsername("username");
        user2.setAuthorities(authorities);
        assertEquals(user, user2);

        User user3 = new User();
        user3.setUsername("usernameX");
        user3.setAuthorities(authorities);
        assertNotEquals(user, user3);

        User user4 = new User();
        user4.setUsername("username");
        authority = new Authority();
        authority.setName("ROLE_USER");
        authorities = new Authority[]{authority};
        user4.setAuthorities(authorities);
        assertNotEquals(user, user4);
    }

    @Test
    public void testHashCode() {
        User user = new User();
        user.setUsername("username");
        User user2 = new User();
        user2.setUsername("username");
        assertEquals(user.hashCode(), user2.hashCode());
    }
}