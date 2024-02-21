package com.cvut.cz.fel.ear.instumentalshop.exception;

public class UnauthorizedProducerException extends RuntimeException{
    public UnauthorizedProducerException(){
        super("The producer is not authorized");
    }
}
