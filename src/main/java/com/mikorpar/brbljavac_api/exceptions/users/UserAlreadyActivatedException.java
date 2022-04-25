package com.mikorpar.brbljavac_api.exceptions.users;

public class UserAlreadyActivatedException extends Exception{
    public UserAlreadyActivatedException(String message) {
        super(message);
    }
}
