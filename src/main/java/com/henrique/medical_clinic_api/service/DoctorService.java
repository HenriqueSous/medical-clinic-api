package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.DoctorNotFoundException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public void delete(long id) {
        doctorRepository.deleteById(id);
    }
}
