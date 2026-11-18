package com.henrique.medical_clinic_api.features.patient;

import com.henrique.medical_clinic_api.shared.domain.ProfileStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProfileStatus.class})
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(target = "status", expression = "java(ProfileStatus.ACTIVE)")
    Patient toEntity(PatientRequestDTO patientRequestDTO);

    PatientResponseDTO toResponse(Patient patient);

    PatientSummaryDTO toSummary(Patient patient);

    List<PatientSummaryDTO> toSummaryList(List<Patient> patients);

    List<Patient> toEntityList(List<PatientRequestDTO> patientRequestDTOList);

    List<PatientResponseDTO> toResponseList(List<Patient> patients);
}
