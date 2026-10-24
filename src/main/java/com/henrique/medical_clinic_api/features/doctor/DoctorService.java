package com.henrique.medical_clinic_api.features.doctor;

import com.henrique.medical_clinic_api.features.shared.exception.DuplicateResourceException;
import com.henrique.medical_clinic_api.features.shared.exception.ResourceNotFoundException;
import com.henrique.medical_clinic_api.features.shared.exception.ImmutableFieldException;
import com.henrique.medical_clinic_api.features.specialty.SpecialtyService;
import com.henrique.medical_clinic_api.features.specialty.Specialty;
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
    public Doctor save(Doctor doctor, List<String> specialtiesName) {
        List<Specialty> specialtiesToBeSaved = new ArrayList<>();

        List<Doctor> doctors = find(new DoctorQueryFilter(null, doctor.getCrm(), doctor.getUf()));
        if (!doctors.isEmpty()) {
            throw new DuplicateResourceException("Doctor", "CRM", String.format("%s-%s", doctor.getCrm(), doctor.getUf()));
        }

        for (String specialtyName : specialtiesName) {
            List<Specialty> specialties = specialtyService.findByOptionalFilters(specialtyName, null);

            if (!specialties.isEmpty()) {
                if (specialties.size() > 1) {
                    throw new DuplicateResourceException("Specialty", "name", specialtyName);
                }

                Specialty specialtyFound = specialties.getFirst();
                specialtyFound.getDoctors().add(doctor);
                specialtiesToBeSaved.add(specialtyFound);
            } else {
                throw new ResourceNotFoundException("Specialty with name '" + specialtyName + "' not found");
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
                    specialty.getDoctors().remove(doctor);
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
