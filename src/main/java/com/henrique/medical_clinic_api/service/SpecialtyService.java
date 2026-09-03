package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.SpecialtyNotFoundException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialtyService {
    @Autowired
    private SpecialtyRepository specialtyRepository;

    public List<Specialty> findAll() {
        return specialtyRepository.findAll();
    }

    public Specialty findById(long id) {
        return specialtyRepository.findById(id).orElseThrow(() -> new SpecialtyNotFoundException(id));
    }

    public List<Doctor> findDoctors(long id) {
        Specialty specialty = findById(id);
        return specialty.getDoctors();
    }

    public Specialty save(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    public List<Specialty> findByOptionalFilters(String name, String description) {
        return specialtyRepository.findByOptionalFilters(name, description);
    }

    public Specialty updateInParts(long id, JsonNode jsonNode) {
        Specialty specialty = findById(id);

        if (jsonNode.has("description")) {
            String description = jsonNode.get("description").asString();
            specialty.setDescription(description);
        }

        return specialtyRepository.save(specialty);
    }

    public void delete(long id) {
        Specialty specialty = findById(id);
        specialtyRepository.delete(specialty);
    }
}
