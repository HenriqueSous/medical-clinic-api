package com.henrique.medical_clinic_api.dto.patient;

import jakarta.validation.constraints.NotBlank;

public record PatientRequestDTO(
        @NotBlank(message = "The patient name is mandatory")
        String name,
        @NotBlank(message = "The patient cpf is mandatory")
        String cpf
) {
}
