package com.henrique.medical_clinic_api.dto.consultation;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultationRequestDTO(
        @NotNull(message = "The patientId is mandatory")
        Long patientId,
        @NotNull(message = "The doctorId is mandatory ")
        Long doctorId,
        @FutureOrPresent(message = "The consultation date must be a date in the present or the future")
        LocalDate consultationDate,
        LocalTime consultationTime,
        @Min(value = 15, message = "The minimum duration of a consultation is 15 minutes")
        Integer duration
) {
}
