package com.henrique.medical_clinic_api.dto.consultation;

import com.henrique.medical_clinic_api.dto.doctor.DoctorSummaryDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientSummaryDTO;
import com.henrique.medical_clinic_api.model.ConsultationStatus;

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
