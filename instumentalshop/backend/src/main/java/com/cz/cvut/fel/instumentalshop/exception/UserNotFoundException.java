package com.cz.cvut.fel.instumentalshop.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class UserNotFoundException extends RuntimeException {
    private Set<String> missingUsernames;

    public UserNotFoundException(String message, Set<String> missingUsernames) {
        super(message);
        this.missingUsernames = missingUsernames;
    }

    public UserNotFoundException(String message) {
        super(message);
    }

}