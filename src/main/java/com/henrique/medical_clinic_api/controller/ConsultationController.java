package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationRequestDTO;
import com.henrique.medical_clinic_api.dto.consultation.ConsultationResponseDTO;
import com.henrique.medical_clinic_api.mapper.ConsultationMapper;
import com.henrique.medical_clinic_api.model.ConsultationStatus;
import com.henrique.medical_clinic_api.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consultations")
public class ConsultationController {
    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private ConsultationService consultationService;

    @GetMapping
    private ResponseEntity<List<ConsultationResponseDTO>> get(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) LocalDate consultationDate,
            @RequestParam(required = false) LocalTime consultationTime,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer duration
    ) {
        if (patientId != null && doctorId != null && consultationDate != null && consultationTime != null && status != null && duration != null) {
            return ResponseEntity.ok(consultationMapper.toResponseList(consultationService.findAll()));
        }
        return ResponseEntity.ok(consultationMapper.toResponseList(consultationService.findByOptionalFilters(
                patientId,
                doctorId,
                consultationDate,
                consultationTime,
                status,
                duration
        )));
    }

    @GetMapping("/{id}")
    private ResponseEntity<ConsultationResponseDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(consultationMapper.toResponse(consultationService.findById(id)));
    }

    @PostMapping
    private ResponseEntity<ConsultationResponseDTO> post(@RequestBody ConsultationRequestDTO consultationRequestDTO) {
        return ResponseEntity.ok(consultationMapper.toResponse(consultationService.save(consultationRequestDTO)));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> delete(@PathVariable long id) {
        consultationService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
