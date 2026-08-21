package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}
