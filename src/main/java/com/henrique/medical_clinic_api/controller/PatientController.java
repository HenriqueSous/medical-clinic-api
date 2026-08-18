package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationResponseDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientRequestDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientResponseDTO;
import com.henrique.medical_clinic_api.mapper.PatientMapper;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("patients")
public class PatientController {
    @Autowired
    private PatientService patientService;

    private final PatientMapper patientMapper = PatientMapper.INSTANCE;

    @PostMapping
    private ResponseEntity<PatientResponseDTO> post(@RequestBody PatientRequestDTO patientRequestDTO) {

        Patient patient = patientMapper.patientRequestDtoToPatient(patientRequestDTO);

        Patient patientSaved = patientService.savePatient(patient);

        List<ConsultationResponseDTO> list = new ArrayList<>();

        PatientResponseDTO patientResponseDTO = new PatientResponseDTO(
                patientSaved.getId(),
                patientRequestDTO.name(),
                patientSaved.getCpf(),
                patientSaved.getCreatedAt(),
                patientSaved.getUpdatedAt(),
                list
        );

        return new ResponseEntity<>(patientResponseDTO, HttpStatus.CREATED);
    }
}
