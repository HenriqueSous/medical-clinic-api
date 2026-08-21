package com.henrique.medical_clinic_api.dto.doctor;

public record DoctorSummaryDTO(
        Long id,
        String name,
        String crm,
        String uf
) {
}
