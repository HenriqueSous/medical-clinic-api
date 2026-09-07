package com.henrique.medical_clinic_api.dto.specialty;

import jakarta.validation.constraints.NotBlank;

public record SpecialtyRequestDtO(
        @NotBlank(message = "The specialty name is mandatory")
        String name,
        @NotBlank(message = "The specialty description is mandatory")
        String description
) {
}
