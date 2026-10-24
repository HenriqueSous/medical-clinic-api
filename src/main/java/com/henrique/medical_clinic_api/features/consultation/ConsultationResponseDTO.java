package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.doctor.DoctorSummaryDTO;
import com.henrique.medical_clinic_api.features.patient.PatientSummaryDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ConsultationResponseDTO(
        Long id,
        DoctorSummaryDTO doctor,
        PatientSummaryDTO patient,
        LocalDate consultationDate,
        LocalTime consultationTime,
        Integer duration,
        ConsultationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
