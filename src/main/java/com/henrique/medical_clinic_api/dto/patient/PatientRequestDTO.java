package com.henrique.medical_clinic_api.dto.patient;

public record PatientRequestDTO(
        String name,
        String cpf
) {
}
