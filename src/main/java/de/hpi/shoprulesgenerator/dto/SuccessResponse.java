package de.hpi.shoprulesgenerator.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({ "code", "status", "data", "message" })
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class SuccessResponse<T> extends EmptySuccessResponse {

    private T data;

    public SuccessResponse(T data) {
        setData(data);
    }

}
