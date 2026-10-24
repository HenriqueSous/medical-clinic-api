package com.henrique.medical_clinic_api.features.doctor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = ArrayList.class)
public interface DoctorMapper {
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Mapping(target = "specialties", expression = "java(new ArrayList())")
    Doctor toEntity(DoctorRequestDTO doctorRequestDTO);

    DoctorResponseDTO toResponse(Doctor doctor);

    DoctorSummaryDTO toSummary(Doctor doctor);

    List<DoctorSummaryDTO> toSummaryList(List<Doctor> doctors);

    List<DoctorResponseDTO> toResponseList(List<Doctor> doctors);
}
