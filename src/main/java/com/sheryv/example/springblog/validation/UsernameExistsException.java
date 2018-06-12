package com.sheryv.example.springblog.validation;

public final class UsernameExistsException extends IllegalArgumentException {

    private static final long serialVersionUID = 5861310537366287163L;

    public UsernameExistsException() {
        super();
    }

    public UsernameExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UsernameExistsException(final String message) {
        super(message);
    }

    public UsernameExistsException(final Throwable cause) {
        super(cause);
    }

}
