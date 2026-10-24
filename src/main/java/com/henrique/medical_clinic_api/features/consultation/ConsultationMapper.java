package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.doctor.DoctorMapper;
import com.henrique.medical_clinic_api.features.patient.PatientMapper;
import com.henrique.medical_clinic_api.features.specialty.SpecialtyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class, PatientMapper.class, SpecialtyMapper.class, ConsultationStatus.class})
public interface ConsultationMapper {
    ConsultationMapper INSTANCE = Mappers.getMapper(ConsultationMapper.class);

    ConsultationResponseDTO toResponse(Consultation consultation);

    @Mapping(target = "status", expression = "java(ConsultationStatus.SCHEDULED)")
    @Mapping(target = "duration", source = "duration")
    Consultation toEntity(ConsultationRequestDTO consultationRequestDTO, Integer duration);

    List<ConsultationResponseDTO> toResponseList(List<Consultation> consultations);
}
