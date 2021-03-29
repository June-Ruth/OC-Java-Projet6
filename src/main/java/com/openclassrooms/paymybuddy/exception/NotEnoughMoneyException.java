package com.openclassrooms.paymybuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughMoneyException extends RuntimeException {
    /**
     * Exception to throw when balance money is not enough for a transfer.
     * @param message .
     */
    public NotEnoughMoneyException(final String message) {
        super(message);
    }
}
