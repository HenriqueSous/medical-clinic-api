package com.henrique.medical_clinic_api.mapper;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationRequestDTO;
import com.henrique.medical_clinic_api.dto.consultation.ConsultationResponseDTO;
import com.henrique.medical_clinic_api.model.Consultation;
import com.henrique.medical_clinic_api.model.ConsultationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class, PatientMapper.class, SpecialtyMapper.class, ConsultationStatus.class})
public interface ConsultationMapper {
    ConsultationMapper INSTANCE = Mappers.getMapper(ConsultationMapper.class);

    ConsultationResponseDTO toResponse(Consultation consultation);

    @Mapping(target = "status", expression = "java(ConsultationStatus.SCHEDULED)")
    Consultation toEntity(ConsultationRequestDTO consultationRequestDTO);
}
