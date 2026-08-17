package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationResponseDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientRequestDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientResponseDTO;
import com.henrique.medical_clinic_api.model.Consultation;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    public PatientResponseDTO savePatient(PatientRequestDTO patientRequestDTO) {
        Patient patient = Patient.builder()
                .name(patientRequestDTO.name())
                .cpf(patientRequestDTO.cpf())
                .build();

        Patient patientSaved = patientRepository.save(patient);

        List<ConsultationResponseDTO> list = new ArrayList<>();

        return new PatientResponseDTO(
                patientSaved.getId(),
                patientRequestDTO.name(),
                patientSaved.getCpf(),
                patientSaved.getCreatedAt(),
                patientSaved.getUpdatedAt(),
                list
        );
    }
}
