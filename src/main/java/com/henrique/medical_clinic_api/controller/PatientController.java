package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.patient.PatientRequestDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientResponseDTO;
import com.henrique.medical_clinic_api.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("patients")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @PostMapping
    private ResponseEntity<PatientResponseDTO> post(@RequestBody PatientRequestDTO patientRequestDTO) {
        return new ResponseEntity<>(patientService.savePatient(patientRequestDTO), HttpStatus.CREATED);
    }
}
