package com.musala.drones.domain.ues.model.dto;

import com.musala.drones.domain.ues.model.exception.DroneAppException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@ToString
@Jacksonized
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final String timestamp;
    private final String path;
    private final String serviceCode;
    private final String httpErrorCode;
    private final String message;

    public ErrorResponse(DroneAppException ex, String serviceCode, WebRequest request) {
        this.serviceCode = serviceCode;
        this.httpErrorCode = String.valueOf(ex.getStatus().value());
        this.message = ex.getMessage();
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.path = getPath(request);
    }

    private String getPath(WebRequest r) {
        try {
            ServletWebRequest r1 = (ServletWebRequest) r;
            return ((HttpServletRequest) r1.getNativeRequest()).getRequestURI();
        } catch (Throwable t) {
            log.error("Error when trying get http-request uri: {}", t.getMessage());
            return "unknown";
        }
    }
}

