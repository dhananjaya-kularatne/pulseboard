package com.pulseboard.pulseboard_api.dto;

import com.pulseboard.pulseboard_api.model.HttpMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import lombok.Data;

/**
 * Request payload for creating a new Monitor.
 */
@Data
public class CreateMonitorRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "URL is required")
    @URL(message = "Must be a valid URL")
    private String url;

    @NotNull(message = "Method is required")
    private HttpMethod method;

    @NotNull(message = "Expected status code is required")
    private Integer expectedStatusCode;
}