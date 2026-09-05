package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.domain.SpecialtyAlreadyExistsException;
import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Service
public class SpecialtyService {
    @Autowired
    private SpecialtyRepository specialtyRepository;

    public List<Specialty> findAll() {
        return specialtyRepository.findAll();
    }

    public Specialty findById(long id) {
        return specialtyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
    }

    public List<Doctor> findDoctors(long id) {
        Specialty specialty = findById(id);
        return specialty.getDoctors();
    }

    public Specialty save(Specialty specialty) {
        List<Specialty> specialties = findByOptionalFilters(specialty.getName(), null);
        if (!specialties.isEmpty()) {
            throw new SpecialtyAlreadyExistsException(specialty.getName());
        }
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
