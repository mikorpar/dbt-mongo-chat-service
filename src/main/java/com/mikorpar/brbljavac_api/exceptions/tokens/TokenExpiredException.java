package com.mikorpar.brbljavac_api.exceptions.tokens;

public class TokenExpiredException extends Exception{
    public TokenExpiredException(String message) {
        super(message);
    }
}
