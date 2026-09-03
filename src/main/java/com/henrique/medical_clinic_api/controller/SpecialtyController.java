package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.specialty.SpecialtyRequestDtO;
import com.henrique.medical_clinic_api.dto.specialty.SpecialtyResponseDTO;
import com.henrique.medical_clinic_api.mapper.SpecialtyMapper;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    private ResponseEntity<SpecialtyResponseDTO> post(@RequestBody SpecialtyRequestDtO specialtyRequestDtO) {
        Specialty specialty = specialtyMapper.toEntity(specialtyRequestDtO);
        return ResponseEntity.ok(specialtyMapper.toResponse(specialtyService.save(specialty)));
    }

    @GetMapping
    private ResponseEntity<List<SpecialtyResponseDTO>> get() {
        return ResponseEntity.ok(specialtyMapper.toResponseList(specialtyService.findAll()));
    }

    @GetMapping("/{id}")
    private ResponseEntity<SpecialtyResponseDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(specialtyMapper.toResponse(specialtyService.findById(id)));
    }
}
