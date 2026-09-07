package com.henrique.medical_clinic_api.dto.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@JsonPropertyOrder({"timestamp", "status", "error", "message", "fields", "path"})
public class ValidationExceptionResponseDTO {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private Map<String, String> fields;
    private String path;
}
