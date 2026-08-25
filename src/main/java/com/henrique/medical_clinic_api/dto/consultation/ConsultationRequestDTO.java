package com.henrique.medical_clinic_api.dto.consultation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultationRequestDTO(
        Long patientId,
        Long doctorId,
        LocalDate consultationDate,
        LocalTime consultationTime,
        Integer duration
) {
}
