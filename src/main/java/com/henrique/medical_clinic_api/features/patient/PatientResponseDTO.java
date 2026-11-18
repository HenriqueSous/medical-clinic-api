package com.henrique.medical_clinic_api.features.patient;

import com.henrique.medical_clinic_api.shared.domain.ProfileStatus;

import java.time.LocalDateTime;

public record PatientResponseDTO(
        Long id,
        String name,
        String cpf,
        ProfileStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
