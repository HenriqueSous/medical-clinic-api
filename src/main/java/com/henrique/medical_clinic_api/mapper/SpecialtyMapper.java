package com.henrique.medical_clinic_api.mapper;

import com.henrique.medical_clinic_api.dto.specialty.SpecialtyRequestDtO;
import com.henrique.medical_clinic_api.dto.specialty.SpecialtyResponseDTO;
import com.henrique.medical_clinic_api.model.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = DoctorMapper.class)
public interface SpecialtyMapper {
    SpecialtyMapper INSTANCE = Mappers.getMapper(SpecialtyMapper.class);

    Specialty toEntity(SpecialtyRequestDtO specialtyRequestDtO);

    SpecialtyResponseDTO toResponse(Specialty specialty);

    List<Specialty> toEntityList(List<SpecialtyRequestDtO> specialtyRequestDtOList);

    List<SpecialtyResponseDTO> toResponseList(List<Specialty> specialties);
}
