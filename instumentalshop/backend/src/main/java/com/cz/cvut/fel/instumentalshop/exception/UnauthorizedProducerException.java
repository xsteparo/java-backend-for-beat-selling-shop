package com.cz.cvut.fel.instumentalshop.exception;

public class UnauthorizedProducerException extends RuntimeException{
    public UnauthorizedProducerException(){
        super("The producer is not authorized");
    }
}
