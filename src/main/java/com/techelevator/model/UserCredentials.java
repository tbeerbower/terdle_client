package com.techelevator.model;

/**
 * Model class representing a user's login credentials - username & password.
 *
 * This is an example of a DTO, a Data Transfer Object, which is a class that is made
 * specifically to send data between the client and server.
 */
public class UserCredentials {

   private String username;
   private String password;

   public UserCredentials(String username, String password) {
      this.username = username;
      this.password = password;
   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }
}
