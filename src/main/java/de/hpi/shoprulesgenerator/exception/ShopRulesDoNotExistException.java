package de.hpi.shoprulesgenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ShopRulesDoNotExistException extends Exception {

    ShopRulesDoNotExistException(String message) {
        super(message);
    }
}
