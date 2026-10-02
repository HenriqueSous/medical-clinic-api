package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.DuplicateResourceException;
import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.exception.validation.ImmutableFieldException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.queryFilters.DoctorQueryFilter;
import com.henrique.medical_clinic_api.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecialtyService specialtyService;

    public List<Doctor> find(DoctorQueryFilter doctorQueryFilter) {
        return doctorRepository.findAll(doctorQueryFilter.toSpecification());
    }

    public Doctor findById(long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
    }

    public List<Specialty> findSpecialties(long id) {
        return findById(id).getSpecialties();
    }

    @Transactional
    public Doctor save(Doctor doctor) {
        List<Specialty> specialtiesToBeSaved = new ArrayList<>();

        List<Doctor> doctors = find(new DoctorQueryFilter(null, doctor.getCrm(), doctor.getUf()));
        if (!doctors.isEmpty()) {
            throw new DuplicateResourceException("Doctor", "CRM", String.format("%s-%s", doctor.getCrm(), doctor.getUf()));
        }

        for (Specialty specialty : doctor.getSpecialties()) {
            List<Specialty> specialties = specialtyService.findByOptionalFilters(specialty.getName(), null);

            if (!specialties.isEmpty()) {
                if (specialties.size() > 1) {
                    throw new DuplicateResourceException("Specialty", "name", specialty.getName());
                }

                Specialty specialtyByName = specialties.getFirst();
                specialtyByName.getDoctors().add(doctor);

                specialtiesToBeSaved.add(specialtyByName);
            } else {
                if (!specialtiesToBeSaved.contains(specialty)) {
                    specialty.getDoctors().add(doctor);
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

        if (jsonNode.has("crm") || jsonNode.has("uf")) {
            throw new ImmutableFieldException("CRM and UF cannot be changed after doctor creation");
        }
        if (jsonNode.has("name")) {
            String name = jsonNode.get("name").asString();
            doctor.setName(name);
        }
        if (jsonNode.has("specialties")) {
            JsonNode specialties = jsonNode.path("specialties");

            if (specialties.has("add")) {
                // Ainda não cria nova specialty automaticamente
                for (JsonNode specialtyToAdd : specialties.get("add").asArray()) {
                    List<Specialty> specialtyList = specialtyService.findByOptionalFilters(specialtyToAdd.asString(), null);

                    if (specialtyList.isEmpty()) {
                        throw new ResourceNotFoundException("Specialty with name '" + specialtyToAdd.asString() + "' not found");
                    }
                    Specialty specialty = specialtyList.getFirst();

                    if (!doctor.getSpecialties().contains(specialty)) {
                        specialty.getDoctors().add(doctor);
                        doctor.getSpecialties().add(specialty);
                    }
                }
            }
            if (specialties.has("remove")) {
                for (JsonNode specialtyToRemove : specialties.get("remove").asArray()) {
                    List<Specialty> specialtyList = specialtyService.findByOptionalFilters(specialtyToRemove.asString(), null);

                    if (specialtyList.isEmpty()) {
                        throw new ResourceNotFoundException("Specialty with name '" + specialtyToRemove.asString() + "' not found");
                    }
                    Specialty specialty = specialtyList.getFirst();

                    doctor.getSpecialties().remove(specialty);
                }
            }
        }

        return doctorRepository.save(doctor);
    }

    public void delete(long id) {
        Doctor doctor = findById(id);
        doctorRepository.delete(doctor);
    }
}
