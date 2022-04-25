package com.mikorpar.brbljavac_api.exceptions.files;

public class UserNotFileOwnerException extends Exception {
    public UserNotFileOwnerException(String message) {
        super(message);
    }
}
