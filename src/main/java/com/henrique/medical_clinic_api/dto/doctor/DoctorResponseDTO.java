package com.henrique.medical_clinic_api.dto.doctor;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationResponseDTO;
import com.henrique.medical_clinic_api.dto.specialty.SpecialtySummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record DoctorResponseDTO(
        Long id,
        String name,
        String crm,
        String uf,
        List<ConsultationResponseDTO> consultations,
        List<SpecialtySummaryDTO> specialties,
        LocalDateTime updatedAt,
        LocalDateTime createdAt
) {
}
