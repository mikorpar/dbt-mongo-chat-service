package com.mikorpar.brbljavac_api.exceptions.users;

public class UsernameTakenException extends Exception{
    public UsernameTakenException(String message) {
        super(message);
    }
}
