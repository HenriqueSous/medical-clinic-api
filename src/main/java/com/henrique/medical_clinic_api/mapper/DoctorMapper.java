package com.henrique.medical_clinic_api.mapper;

import com.henrique.medical_clinic_api.dto.doctor.DoctorRequestDTO;
import com.henrique.medical_clinic_api.dto.doctor.DoctorResponseDTO;
import com.henrique.medical_clinic_api.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    Doctor toEntity(DoctorRequestDTO doctorRequestDTO);

    DoctorResponseDTO toResponse(Doctor doctor);

    List<Doctor> toEntityList(List<DoctorRequestDTO> doctorRequestDTOList);

    List<DoctorResponseDTO> toResponseList(List<Doctor> doctors);
}
