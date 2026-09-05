package com.henrique.medical_clinic_api.dto.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonPropertyOrder({"timestamp", "status", "error", "message", "path"})
public class ExceptionResponseDTO {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
