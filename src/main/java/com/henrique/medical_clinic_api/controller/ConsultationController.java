package com.henrique.medical_clinic_api.controller;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationRequestDTO;
import com.henrique.medical_clinic_api.dto.consultation.ConsultationResponseDTO;
import com.henrique.medical_clinic_api.mapper.ConsultationMapper;
import com.henrique.medical_clinic_api.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consultations")
public class ConsultationController {
    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private ConsultationService consultationService;

    @PostMapping
    private ResponseEntity<ConsultationResponseDTO> post(@RequestBody ConsultationRequestDTO consultationRequestDTO) {
        return ResponseEntity.ok(consultationMapper.toResponse(consultationService.save(consultationRequestDTO)));
    }
}
