package com.cvut.cz.fel.ear.instumentalshop.exception;

import lombok.Getter;

@Getter
public class InvalidProfitPercentageException extends RuntimeException{
    private final Integer totalPercentage;
    public InvalidProfitPercentageException(String message, Integer totalPercentage) {
        super(message);
        this.totalPercentage = totalPercentage;
    }

}
