package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.doctor.DoctorRequestDTO;
import com.henrique.medical_clinic_api.dto.doctor.DoctorResponseDTO;
import com.henrique.medical_clinic_api.dto.specialty.SpecialtySummaryDTO;
import com.henrique.medical_clinic_api.mapper.DoctorMapper;
import com.henrique.medical_clinic_api.mapper.SpecialtyMapper;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @Autowired
    private SpecialtyMapper specialtyMapper;

    private final DoctorMapper doctorMapper = DoctorMapper.INSTANCE;

    @GetMapping
    private ResponseEntity<List<DoctorResponseDTO>> get(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String crm,
            @RequestParam(required = false) String uf
    ) {
        if (name == null && crm == null && uf == null) {
            return ResponseEntity.ok(doctorMapper.toResponseList(doctorService.findAll()));
        }
        return ResponseEntity.ok(doctorMapper.toResponseList(doctorService.findByOptionalFilters(name, crm, uf)));
    }

    @GetMapping("/{id}")
    private ResponseEntity<DoctorResponseDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(doctorMapper.toResponse(doctorService.findById(id)));
    }

    @GetMapping("/{id}/specialties")
    private ResponseEntity<List<SpecialtySummaryDTO>> getSpecialties(@PathVariable long id) {
        return ResponseEntity.ok(specialtyMapper.toSummaryList(doctorService.findSpecialties(id)));
    }

    @PostMapping
    private ResponseEntity<DoctorResponseDTO> post(@RequestBody DoctorRequestDTO doctorRequestDTO) {
        Doctor doctor = doctorMapper.toEntity(doctorRequestDTO);
        return ResponseEntity.ok(doctorMapper.toResponse(doctorService.save(doctor)));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> delete(@PathVariable long id) {
        doctorService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
