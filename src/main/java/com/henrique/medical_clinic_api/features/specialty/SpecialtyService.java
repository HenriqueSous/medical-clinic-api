package com.henrique.medical_clinic_api.features.specialty;

import com.henrique.medical_clinic_api.shared.exception.DuplicateResourceException;
import com.henrique.medical_clinic_api.shared.exception.ResourceNotFoundException;
import com.henrique.medical_clinic_api.shared.exception.BodyEmptyException;
import com.henrique.medical_clinic_api.shared.exception.ImmutableFieldException;
import com.henrique.medical_clinic_api.features.doctor.Doctor;
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
        return findById(id).getDoctors();
    }

    public Specialty save(Specialty specialty) {
        List<Specialty> specialties = findByOptionalFilters(specialty.getName(), null);
        if (!specialties.isEmpty()) {
            throw new DuplicateResourceException("Specialty", "name", specialty.getName());
        }
        return specialtyRepository.save(specialty);
    }

    public List<Specialty> findByOptionalFilters(String name, String description) {
        return specialtyRepository.findByOptionalFilters(name, description);
    }

    public Specialty updateByParts(long id, JsonNode jsonNode) {
        if (jsonNode.isEmpty()) throw new BodyEmptyException();
        Specialty specialty = findById(id);

        if (jsonNode.has("name")) {
            throw new ImmutableFieldException("name cannot be changed after specialty creation");
        }
        if (jsonNode.has("description")) {
            String description = jsonNode.get("description").asString();
            specialty.setDescription(description);
        }

        return specialtyRepository.save(specialty);
    }

    public void delete(long id) {
        Specialty specialty = findById(id);

        for (Doctor doctor : specialty.getDoctors()) {
            doctor.getSpecialties().remove(specialty);
        }

        specialtyRepository.delete(specialty);
    }
}
