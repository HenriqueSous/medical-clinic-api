package com.henrique.medical_clinic_api.dto.patient;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationResponseDTO;

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
