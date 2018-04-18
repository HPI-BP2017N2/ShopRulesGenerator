package de.hpi.shoprulesgenerator.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonPropertyOrder({ "code", "status", "message"})
public class EmptySuccessResponse extends AbstractResponse {

    public ResponseEntity<Object> send() {
        return super.send(HttpStatus.OK);
    }

    @Override
    public EmptySuccessResponse withMessage(String message) {
        return (EmptySuccessResponse) super.withMessage(message);
    }
}
