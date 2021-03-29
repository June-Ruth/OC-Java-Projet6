package com.openclassrooms.paymybuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ElementNotFoundException extends RuntimeException {
    /**
     * Exception to throw when try to find
     * a not existing element as user account, transfer...
     * @param message .
     */
    public ElementNotFoundException(final String message) {
        super(message);
    }
}
