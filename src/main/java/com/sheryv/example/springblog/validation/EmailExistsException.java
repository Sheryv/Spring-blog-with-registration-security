package com.sheryv.example.springblog.validation;

@SuppressWarnings("serial")
public class EmailExistsException extends IllegalArgumentException {

    public EmailExistsException(final String message) {
        super(message);
    }

}
