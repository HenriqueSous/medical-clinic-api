package com.henrique.medical_clinic_api.dto.patient;

public record PatientSummaryDTO(
        Long id,
        String name,
        String cpf
){
}
