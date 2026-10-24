package com.henrique.medical_clinic_api.features.specialty;

import com.henrique.medical_clinic_api.features.doctor.DoctorSummaryDTO;
import com.henrique.medical_clinic_api.features.doctor.DoctorMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

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
    private ResponseEntity<SpecialtyResponseDTO> post(@Valid @RequestBody SpecialtyRequestDTO specialtyRequestDtO) {
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

    @PatchMapping("/{id}")
    private ResponseEntity<SpecialtyResponseDTO> patch(@PathVariable long id, @RequestBody JsonNode jsonNode) {
        return ResponseEntity.ok(specialtyMapper.toResponse(specialtyService.updateByParts(id, jsonNode)));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> delete(@PathVariable long id) {
        specialtyService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
