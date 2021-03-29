package com.openclassrooms.paymybuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ElementAlreadyExistsException extends RuntimeException {

    /**
     * Exception to throw when try to save a already existing element.
     * @param message .
     */
    public ElementAlreadyExistsException(final String message) {
        super(message);
    }
}
