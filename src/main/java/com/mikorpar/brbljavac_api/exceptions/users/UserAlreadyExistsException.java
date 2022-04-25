package com.mikorpar.brbljavac_api.exceptions.users;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
