package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.patient.PatientRequestDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientResponseDTO;
import com.henrique.medical_clinic_api.mapper.PatientMapper;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("patients")
public class PatientController {
    @Autowired
    private PatientService patientService;

    private final PatientMapper patientMapper = PatientMapper.INSTANCE;

    @PostMapping
    private ResponseEntity<PatientResponseDTO> post(@RequestBody PatientRequestDTO patientRequestDTO) {
        Patient patient = patientMapper.toEntity(patientRequestDTO);
        PatientResponseDTO patientSaved = patientMapper.toResponse(patientService.savePatient(patient));

        return new ResponseEntity<>(patientSaved, HttpStatus.CREATED);
    }
}
