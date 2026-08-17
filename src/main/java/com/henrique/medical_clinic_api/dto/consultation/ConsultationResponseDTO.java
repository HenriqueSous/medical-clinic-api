package com.henrique.medical_clinic_api.dto.consultation;

import com.henrique.medical_clinic_api.model.ConsultationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ConsultationResponseDTO(
        Integer id,
        DoctorConsultationResponseDTO doctor,
        PatientConsultationResponseDTO patient,
        LocalDate consultationDate,
        LocalTime consultationTime,
        Integer duration,
        ConsultationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
