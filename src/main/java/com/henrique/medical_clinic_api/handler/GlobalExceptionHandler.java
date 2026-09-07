package com.henrique.medical_clinic_api.handler;

import com.henrique.medical_clinic_api.dto.exception.ExceptionResponseDTO;
import com.henrique.medical_clinic_api.dto.exception.ValidationExceptionResponseDTO;
import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String path = request.getDescription(false).replace("uri=", "");

        return new ResponseEntity<>(
                ValidationExceptionResponseDTO.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error(HttpStatus.BAD_REQUEST.name())
                        .message("Check the fields errors")
                        .fields(errors)
                        .path(path)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
