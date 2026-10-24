package com.henrique.medical_clinic_api.features.specialty;

import com.henrique.medical_clinic_api.features.doctor.DoctorMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = DoctorMapper.class)
public interface SpecialtyMapper {
    SpecialtyMapper INSTANCE = Mappers.getMapper(SpecialtyMapper.class);

    Specialty toEntity(SpecialtyRequestDTO specialtyRequestDtO);

    SpecialtyResponseDTO toResponse(Specialty specialty);

    SpecialtySummaryDTO toSummary(Specialty specialty);

    List<SpecialtyResponseDTO> toResponseList(List<Specialty> specialties);

    List<SpecialtySummaryDTO> toSummaryList(List<Specialty> specialties);
}
