package com.henrique.medical_clinic_api.features.patient;

public record PatientSummaryDTO(
        Long id,
        String name,
        String cpf
){
}
