package com.cz.cvut.fel.instumentalshop.exception;

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