package com.techelevator.model;

/**
 * Model class representing a logged-in, authenticated user.
 *
 * Contains the
 *   - token - the JWT auth token for interactions with the server REST API.
 *   - user - the user object with additional information about the logged-in user.
 */
public class AuthenticatedUser {
	
	private String token;
	private User user;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
