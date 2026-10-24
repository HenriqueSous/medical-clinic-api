package com.henrique.medical_clinic_api.features.doctor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record DoctorRequestDTO(
        @NotBlank(message = "The doctor name is mandatory")
        String name,

        @NotBlank(message = "The doctor CRM is mandatory")
        @Pattern(regexp = "\\d{6}", message = "Invalid CRM")
        String crm,

        @NotBlank(message = "The doctor UF is mandatory")
        @Pattern(
                regexp = "\\b(?:AC|AL|AM|AP|BA|CE|DF|ES|GO|MA|MG|MS|MT|PA|PB|PE|PI|PR|RJ|RN|RO|RR|RS|SC|SE|SP|TO)",
                message = "Invalid UF"
        )
        String uf,

        @Valid
        List<String> specialties
) {
}
