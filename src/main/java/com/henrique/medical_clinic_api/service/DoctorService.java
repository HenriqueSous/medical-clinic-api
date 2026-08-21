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

    @Transactional
    public Doctor save(Doctor doctor) {
        List<Specialty> specialtiesToBeSaved = new ArrayList<>();

        for (Specialty specialty : doctor.getSpecialties()) {
            Specialty specialtyByName = specialtyService.findByName(specialty.getName());

            if (specialtyByName != null) {
                List<Doctor> list = specialtyByName.getDoctors();
                list.add(doctor);
                specialtyByName.setDoctors(list);

                specialtiesToBeSaved.add(specialtyByName);
            } else {
                specialtiesToBeSaved.add(specialty);
            }
        }

        doctor.setSpecialties(specialtiesToBeSaved);
        return doctorRepository.save(doctor);
    }

    public void delete(long id) {
        doctorRepository.deleteById(id);
    }
}
