package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.DoctorNotFoundException;
import com.henrique.medical_clinic_api.exception.SpecialtyNotFoundException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecialtyService specialtyService;

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Doctor findById(long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
    }

    public List<Doctor> findByOptionalFilters(String name, String crm, String uf) {
        return doctorRepository.findByOptionalFilters(name, crm, uf);
    }

    public List<Specialty> findSpecialties(long id) {
        return findById(id).getSpecialties();
    }

    @Transactional
    public Doctor save(Doctor doctor) {
        List<Specialty> specialtiesToBeSaved = new ArrayList<>();

        for (Specialty specialty : doctor.getSpecialties()) {
            List<Specialty> specialties = specialtyService.findByOptionalFilters(specialty.getName(), null);

            if (!specialties.isEmpty()) {
                if (specialties.size() > 1) {
                    throw new InternalError("Duplicate specialties: "+specialties);
                }

                Specialty specialtyByName = specialties.getFirst();
                List<Doctor> list = specialtyByName.getDoctors();
                list.add(doctor);
                specialtyByName.setDoctors(list);

                specialtiesToBeSaved.add(specialtyByName);
            } else {
                if (!specialtiesToBeSaved.contains(specialty)) {
                    specialtiesToBeSaved.add(specialty);
                }
            }
        }

        doctor.setSpecialties(specialtiesToBeSaved);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateByParts(long id, JsonNode jsonNode) {
        Doctor doctor = findById(id);

        if (jsonNode.has("name")) {
            String name = jsonNode.get("name").asString();
            doctor.setName(name);
        }
        if (jsonNode.has("crm")) {
            String crm = jsonNode.get("crm").asString();
            doctor.setCrm(crm);
        }
        if (jsonNode.has("uf")) {
            String uf = jsonNode.get("uf").asString();
            doctor.setUf(uf);
        }
        if (jsonNode.has("specialties")) {
            JsonNode specialties = jsonNode.path("specialties");

            if (specialties.has("add")) {
                // Ainda não cria nova specialty automaticamente
                for (JsonNode specialtyToAdd : specialties.get("add").asArray()) {
                    Optional<Specialty> byName = specialtyService.findByName(specialtyToAdd.asString());

                    Specialty specialty = byName.orElseThrow(
                            () -> new SpecialtyNotFoundException("Specialty with name '" + specialtyToAdd.asString() + "' not found")
                    );

                    specialty.getDoctors().add(doctor);
                    doctor.getSpecialties().add(specialty);
                }
            }
            if (specialties.has("remove")) {
                for (JsonNode specialtyToRemove : specialties.get("remove").asArray()) {
                    Optional<Specialty> byName = specialtyService.findByName(specialtyToRemove.asString());

                    Specialty specialty = byName.orElseThrow(
                            () -> new SpecialtyNotFoundException("Specialty with name '" + specialtyToRemove.asString() + "' not found")
                    );

                    doctor.getSpecialties().remove(specialty);
                }
            }
        }

        return doctorRepository.save(doctor);
    }

    public void delete(long id) {
        doctorRepository.deleteById(id);
    }
}
