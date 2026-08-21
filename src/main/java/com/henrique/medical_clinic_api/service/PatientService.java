package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.PatientNotFoundException;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(long id) {
        return patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
    }

    public List<Patient> findByOptionalFilters(String name, String cpf) {
        return patientRepository.findByOptionalFilters(name, cpf);
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public void deletePatient(long id) {
        Patient patient = findById(id);
        patientRepository.delete(patient);
    }
}
