package de.hpi.shoprulesgenerator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class SuccessCleanResponse extends SuccessResponse<SuccessCleanResponse.CleanResponse> {

    @SuppressWarnings("WeakerAccess")
    @Getter
    @Setter
    public static class CleanResponse {
        private String url;
    }
}
