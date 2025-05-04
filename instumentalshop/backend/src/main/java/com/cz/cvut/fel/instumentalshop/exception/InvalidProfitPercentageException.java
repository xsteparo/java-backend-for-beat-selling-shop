package com.cz.cvut.fel.instumentalshop.exception;

import lombok.Getter;

@Getter
public class InvalidProfitPercentageException extends RuntimeException{
    private final Integer totalPercentage;
    public InvalidProfitPercentageException(String message, Integer totalPercentage) {
        super(message);
        this.totalPercentage = totalPercentage;
    }

}
