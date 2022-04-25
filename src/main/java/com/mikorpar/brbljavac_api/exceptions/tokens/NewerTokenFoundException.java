package com.mikorpar.brbljavac_api.exceptions.tokens;

public class NewerTokenFoundException extends Exception{
    public NewerTokenFoundException(String message) {
        super(message);
    }
}
