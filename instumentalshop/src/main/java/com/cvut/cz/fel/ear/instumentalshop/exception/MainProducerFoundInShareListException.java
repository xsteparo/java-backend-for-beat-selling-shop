package com.cvut.cz.fel.ear.instumentalshop.exception;

import lombok.Getter;

@Getter
public class MainProducerFoundInShareListException extends RuntimeException{
    private final String requestingProducerName;
    public MainProducerFoundInShareListException(String message, String requestingProducerName){
        super(message);
        this.requestingProducerName = requestingProducerName;
    }

}
