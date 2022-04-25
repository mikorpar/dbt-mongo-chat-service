package com.mikorpar.brbljavac_api.exceptions.users;

public class UserNotGroupMemberException extends Exception {
    public UserNotGroupMemberException(String message) {
        super(message);
    }
}
