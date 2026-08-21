package com.henrique.medical_clinic_api.dto.doctor;

import com.henrique.medical_clinic_api.dto.specialty.SpecialtyRequestDtO;

import java.util.List;

public record DoctorRequestDTO(
        String name,
        String crm,
        String uf,
        List<SpecialtyRequestDtO> specialties
) {
}
