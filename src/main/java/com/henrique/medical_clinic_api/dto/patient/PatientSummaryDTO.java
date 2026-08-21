package com.henrique.medical_clinic_api.dto.patient;

public record PatientSummaryDTO(
        Integer id,
        String name,
        String cpf
){
}
