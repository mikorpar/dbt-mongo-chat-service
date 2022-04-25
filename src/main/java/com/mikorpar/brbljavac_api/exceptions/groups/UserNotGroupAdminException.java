package com.mikorpar.brbljavac_api.exceptions.groups;

public class UserNotGroupAdminException extends Exception {
    public UserNotGroupAdminException(String message) {
        super(message);
    }
}
