package com.henrique.medical_clinic_api.mapper;

import com.henrique.medical_clinic_api.dto.patient.PatientRequestDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientResponseDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientSummaryDTO;
import com.henrique.medical_clinic_api.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    Patient toEntity(PatientRequestDTO patientRequestDTO);

    PatientResponseDTO toResponse(Patient patient);

    PatientSummaryDTO toSummary(Patient patient);

    List<PatientSummaryDTO> toSummaryList(List<Patient> patients);

    List<Patient> toEntityList(List<PatientRequestDTO> patientRequestDTOList);

    List<PatientResponseDTO> toResponseList(List<Patient> patients);
}
