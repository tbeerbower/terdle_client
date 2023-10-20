package com.techelevator.services;

import com.techelevator.model.AuthenticatedUser;
import com.techelevator.model.UserCredentials;
import com.techelevator.utils.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

/**
 * AuthenticationService is a class for managing requests made to the server REST API for user login.
 *
 * By encapsulating this logic into a separate class, it becomes easier to manage, maintain, and
 * test the interactions with the server REST API separate from the other application logic.
 **/
public class AuthenticationService {

    private final String API_BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Constructor - The only outside dependency is for the base url used to access the server. Using dependency injection
     * for this value allows it to be more easily changed. It is common for this URL to change for development, testing,
     * and for live deployment.
     *
     * @param url Base URL for the server REST API
     */
    public AuthenticationService(String url) {
        this.API_BASE_URL = url;
    }

    /**
     * Sends a login request to the server REST API.
     * @param credentials user credentials
     * @return the AuthenticatedUser, or null if login failed
     */
    public AuthenticatedUser login(UserCredentials credentials) {
        HttpEntity<UserCredentials> entity = createCredentialsEntity(credentials);
        AuthenticatedUser user = null;
        try {
            ResponseEntity<AuthenticatedUser> response =
                    restTemplate.exchange(API_BASE_URL + "login", HttpMethod.POST, entity, AuthenticatedUser.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    /**
     * Helper method to create the HTTP Entity that bundles the user credentials data to send to the server REST API.
     * @param credentials the user credentials
     * @return HttpEntity containing the credentials
     */
	private HttpEntity<UserCredentials> createCredentialsEntity(UserCredentials credentials) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	return new HttpEntity<>(credentials, headers);
    }
}
