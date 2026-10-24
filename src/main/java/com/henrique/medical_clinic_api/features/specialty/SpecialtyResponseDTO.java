package com.henrique.medical_clinic_api.features.specialty;

import com.henrique.medical_clinic_api.features.doctor.DoctorSummaryDTO;

import java.util.List;

public record SpecialtyResponseDTO(
        Long id,
        String name,
        String description,
        List<DoctorSummaryDTO> doctors
) {
}
