package com.henrique.medical_clinic_api.features.patient;

import com.henrique.medical_clinic_api.features.consultation.ConsultationResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record PatientResponseDTO(
        Long id,
        String name,
        String cpf,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ConsultationResponseDTO> consultations
) {
}
