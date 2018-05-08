package de.hpi.shoprulesgenerator.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class CouldNotDetermineJsonPathException extends Exception {

    public CouldNotDetermineJsonPathException(String message) {
        super(message);
    }
}
