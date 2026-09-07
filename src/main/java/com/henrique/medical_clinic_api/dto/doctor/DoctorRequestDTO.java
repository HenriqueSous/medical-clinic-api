package com.henrique.medical_clinic_api.dto.doctor;

import com.henrique.medical_clinic_api.dto.specialty.SpecialtyRequestDtO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DoctorRequestDTO(
        @NotBlank(message = "The doctor name is mandatory")
        String name,
        @NotBlank(message = "The doctor crm is mandatory")
        String crm,
        @NotBlank(message = "The doctor uf is mandatory")
        String uf,
        @Valid
        List<SpecialtyRequestDtO> specialties
) {
}
