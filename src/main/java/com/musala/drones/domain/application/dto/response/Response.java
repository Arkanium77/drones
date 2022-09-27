package com.musala.drones.domain.application.dto.response;

import com.musala.drones.domain.application.enums.ResponseStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private final ResponseStatus status;
    private final String description;

    public static Response ok(String message) {
        return new Response(ResponseStatus.OK, message);
    }
}
