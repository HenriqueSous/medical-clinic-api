package com.henrique.medical_clinic_api.features.patient;

import java.time.LocalDateTime;

public record PatientResponseDTO(
        Long id,
        String name,
        String cpf,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
