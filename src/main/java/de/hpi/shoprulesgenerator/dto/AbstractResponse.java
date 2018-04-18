package de.hpi.shoprulesgenerator.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractResponse {

    private String message;

    private int code;

    private String status;

    public AbstractResponse withMessage(String message) {
        setMessage(message);
        return this;
    }

    public ResponseEntity<Object> send(HttpStatus status) {
        setCode(status.value());
        setStatus(status.getReasonPhrase());
        return new ResponseEntity<>(this, status);
    }

}
