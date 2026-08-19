package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.patient.PatientRequestDTO;
import com.henrique.medical_clinic_api.dto.patient.PatientResponseDTO;
import com.henrique.medical_clinic_api.mapper.PatientMapper;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("patients")
public class PatientController {
    @Autowired
    private PatientService patientService;

    private final PatientMapper patientMapper = PatientMapper.INSTANCE;

    @GetMapping
    private ResponseEntity<List<PatientResponseDTO>> get(@RequestParam(name = "cpf", required = false) String cpf) {
        List<PatientResponseDTO> response;
        if (cpf == null) {
            response = patientMapper.toResponseList(patientService.findAll());
        } else {
            response = patientMapper.toResponseList(patientService.findByCpf(cpf));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    private ResponseEntity<PatientResponseDTO> getById(@PathVariable long id) {
        PatientResponseDTO response = patientMapper.toResponse(patientService.findById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    private ResponseEntity<PatientResponseDTO> post(@RequestBody PatientRequestDTO patientRequestDTO) {
        Patient patient = patientMapper.toEntity(patientRequestDTO);
        PatientResponseDTO patientSaved = patientMapper.toResponse(patientService.savePatient(patient));

        return new ResponseEntity<>(patientSaved, HttpStatus.CREATED);
    }
}
