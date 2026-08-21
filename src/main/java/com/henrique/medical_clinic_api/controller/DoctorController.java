package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.doctor.DoctorRequestDTO;
import com.henrique.medical_clinic_api.dto.doctor.DoctorResponseDTO;
import com.henrique.medical_clinic_api.mapper.DoctorMapper;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    private final DoctorMapper doctorMapper = DoctorMapper.INSTANCE;

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
