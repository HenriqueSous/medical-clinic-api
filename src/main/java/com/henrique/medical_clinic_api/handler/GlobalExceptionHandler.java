package com.henrique.medical_clinic_api.handler;

import com.henrique.medical_clinic_api.dto.exception.ExceptionResponseDTO;
import com.henrique.medical_clinic_api.dto.exception.ValidationExceptionResponseDTO;
import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponseDTO> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest servlet) {
        HttpStatus status = HttpStatus.CONFLICT;

        String rootMessage = ex.getRootCause().getMessage();
        String message = rootMessage;

        if (rootMessage.contains("patients.cpf")) {
            Pattern pattern = Pattern.compile("'(\\d+)'");
            Matcher matcher = pattern.matcher(rootMessage);
            String cpf = "";
            if (matcher.find()) {
                cpf = matcher.group(1);
            }
            message = String.format("Duplicate Patient with CPF '"+cpf+"'");
        } else if (rootMessage.contains("doctors.uk_crm_uf")) {
            Pattern pattern = Pattern.compile("'(\\d+-\\w+)'");
            Matcher matcher = pattern.matcher(rootMessage);
            String crm = "";
            if (matcher.find()) {
                crm = matcher.group(1);
            }
            message = String.format("Duplicate Doctor with CRM '"+crm+"'");
        }

        return new ResponseEntity<>(
                ExceptionResponseDTO.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error(status.name())
                        .message(message)
                        .path(servlet.getRequestURI())
                        .build(),
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
