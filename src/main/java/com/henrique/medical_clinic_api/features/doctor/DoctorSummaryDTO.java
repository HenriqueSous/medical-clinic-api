package com.henrique.medical_clinic_api.features.doctor;

public record DoctorSummaryDTO(
        Long id,
        String name,
        String crm,
        String uf
) {
}
