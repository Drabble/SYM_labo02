/**
 * Project: Labo 02 SYM
 * Authors: Antoine Drabble & Patrick Djomo
 * Date: 28.11.2016
 */
package com.heig.sym.sym_labo02.model;

/**
 * Defines a simple user with an id, a username, a password and an email
 */
public class User{
    private int id;
    private String username;
    private String password;
    private String email;

    /**
     * Create a new user with the following parameters
     *
     * @param id
     * @param username
     * @param password
     * @param email
     */
    public User(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString(){
        return "Id: " + id + " Username: " + username + " Password: " + password + " Email: " + email;
    }
}
