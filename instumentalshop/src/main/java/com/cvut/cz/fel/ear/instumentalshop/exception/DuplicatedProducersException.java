package com.cvut.cz.fel.ear.instumentalshop.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class DuplicatedProducersException extends RuntimeException {
    private final Set<String> duplicatedUsernames;

    public DuplicatedProducersException(String message, Set<String> duplicatedUsernames) {
        super(message);
        this.duplicatedUsernames = duplicatedUsernames;
    }

}