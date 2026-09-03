package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.doctor.DoctorSummaryDTO;
import com.henrique.medical_clinic_api.dto.specialty.SpecialtyRequestDtO;
import com.henrique.medical_clinic_api.dto.specialty.SpecialtyResponseDTO;
import com.henrique.medical_clinic_api.mapper.DoctorMapper;
import com.henrique.medical_clinic_api.mapper.SpecialtyMapper;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("specialties")
public class SpecialtyController {
    @Autowired
    private SpecialtyService specialtyService;

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @PostMapping
    private ResponseEntity<SpecialtyResponseDTO> post(@RequestBody SpecialtyRequestDtO specialtyRequestDtO) {
        Specialty specialty = specialtyMapper.toEntity(specialtyRequestDtO);
        return ResponseEntity.ok(specialtyMapper.toResponse(specialtyService.save(specialty)));
    }

    @GetMapping
    private ResponseEntity<List<SpecialtyResponseDTO>> get(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {
        if (name == null && description == null) {
            return ResponseEntity.ok(specialtyMapper.toResponseList(specialtyService.findAll()));
        }
        return ResponseEntity.ok(specialtyMapper.toResponseList(specialtyService.findByOptionalFilters(name, description)));
    }

    @GetMapping("/{id}")
    private ResponseEntity<SpecialtyResponseDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(specialtyMapper.toResponse(specialtyService.findById(id)));
    }

    @GetMapping("/{id}/doctors")
    private ResponseEntity<List<DoctorSummaryDTO>> getDoctors(@PathVariable long id) {
        return ResponseEntity.ok(doctorMapper.toSummaryList(specialtyService.findDoctors(id)));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> delete(@PathVariable long id) {
        specialtyService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
