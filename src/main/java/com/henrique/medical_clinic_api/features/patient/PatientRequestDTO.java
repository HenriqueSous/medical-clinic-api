package com.henrique.medical_clinic_api.features.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PatientRequestDTO(
        @NotBlank(message = "The patient name is mandatory")
        String name,
        @NotBlank(message = "The patient cpf is mandatory")
        @Pattern(regexp = "\\d{11}", message = "Invalid CPF")
        String cpf
) {
}
