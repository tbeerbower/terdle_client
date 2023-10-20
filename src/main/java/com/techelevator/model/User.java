package com.techelevator.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Model class representing an application user.
 *
 * Contains information about the user - their username and authorities (user roles).
 */
public class User {

   private int id;
   private String username;
   private Set<Authority> authorities = new HashSet<>();

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public Set<Authority> getAuthorities() {
      return authorities;
   }

   public void setAuthorities(Authority[] authorityArray) {
      for (Authority authority : authorityArray) {
         this.authorities.add(authority);
      }
   }

   public boolean isAdmin() {
      return getAuthorities().contains(Authority.ADMIN_AUTHORITY);
   }

   /*
    * Note that the `equals` method must be overridden in a class to support checking object equality
    * based on the values of its properties - for example here, two User instances are
    * considered equal if they have the same `name` and `authorities` values. If this method is not
    * overridden in a class, the .equals method only returns true when comparing the same exact instance
    * of a class to itself.
    *
    * Note that it is a best practice to ensure that `equals` and `hashcode` methods are consistent by
    * overriding both of them together and using the same properties in both methods to check equality
    * and generate the hash.
    */
   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      User user = (User) o;
      return id == user.id &&
              Objects.equals(username, user.username) &&
              Objects.equals(authorities, user.authorities);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, username, authorities);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", username='" + username + '\'' +
              ", authorities=" + authorities +
              '}';
   }
}
