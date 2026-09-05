package com.henrique.medical_clinic_api.handler;

import com.henrique.medical_clinic_api.dto.exception.ExceptionResponseDTO;
import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponseDTO> handleBusinessException(BusinessException ex, HttpServletRequest servlet) {
        HttpStatus status = ex.getHttpStatus();

        return new ResponseEntity<>(
            ExceptionResponseDTO.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status.value())
                    .error(status.name())
                    .message(ex.getMessage())
                    .path(servlet.getRequestURI()).build(),
            status
        );
    }
}
