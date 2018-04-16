package de.hpi.shoprulesgenerator.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonPropertyOrder({ "code", "status", "message"})
public class ErrorResponse extends AbstractResponse {

    public ErrorResponse withError(Throwable error) {
        setMessage(error.getMessage());
        return this;
    }

    public ResponseEntity<Object> send() {
        return super.send(HttpStatus.BAD_REQUEST);
    }

}
