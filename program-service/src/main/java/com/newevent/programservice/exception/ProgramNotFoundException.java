package com.newevent.programservice.exception;

public class ProgramNotFoundException extends RuntimeException {
    public ProgramNotFoundException(Long id) {
        super("Program not found with id: " + id);
    }
}
