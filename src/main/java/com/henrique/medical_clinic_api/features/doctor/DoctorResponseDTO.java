package com.henrique.medical_clinic_api.features.doctor;

import com.henrique.medical_clinic_api.features.specialty.SpecialtySummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record DoctorResponseDTO(
        Long id,
        String name,
        String crm,
        String uf,
        List<SpecialtySummaryDTO> specialties,
        LocalDateTime updatedAt,
        LocalDateTime createdAt
) {
}
