package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecialtyService {
    @Autowired
    private SpecialtyRepository specialtyRepository;

    public Specialty save(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    public Specialty findByName(String name) {
        return specialtyRepository.findByNameIgnoreCase(name);
    }
}
