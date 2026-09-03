package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.specialty.SpecialtyRequestDtO;
import com.henrique.medical_clinic_api.dto.specialty.SpecialtyResponseDTO;
import com.henrique.medical_clinic_api.mapper.SpecialtyMapper;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
