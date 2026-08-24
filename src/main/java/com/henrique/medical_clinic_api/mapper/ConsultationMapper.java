package com.henrique.medical_clinic_api.mapper;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationRequestDTO;
import com.henrique.medical_clinic_api.dto.consultation.ConsultationResponseDTO;
import com.henrique.medical_clinic_api.model.Consultation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ConsultationMapper {
    ConsultationMapper INSTANCE = Mappers.getMapper(ConsultationMapper.class);

    ConsultationResponseDTO toResponse(Consultation consultation);

    Consultation toEntity(ConsultationRequestDTO consultationRequestDTO);
}
