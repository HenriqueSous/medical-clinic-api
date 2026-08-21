package com.henrique.medical_clinic_api.dto.doctor;

public record DoctorSummaryDTO(
        Integer id,
        String name,
        String crm,
        String uf
) {
}
