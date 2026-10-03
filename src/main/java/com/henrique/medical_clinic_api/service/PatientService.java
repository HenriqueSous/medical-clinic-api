package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.DuplicateResourceException;
import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.exception.validation.BodyEmptyException;
import com.henrique.medical_clinic_api.exception.validation.ImmutableFieldException;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(long id) {
        return patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    public List<Patient> findByOptionalFilters(String name, String cpf) {
        return patientRepository.findByOptionalFilters(name, cpf);
    }

    public Patient save(Patient patient) {
        List<Patient> patients = findByOptionalFilters(null, patient.getCpf());
        if (!patients.isEmpty()) {
            throw new DuplicateResourceException("Patient", "CPF", patient.getCpf());
        }

        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updateByParts(long id, JsonNode jsonNode) {
        if (jsonNode.isEmpty()) throw new BodyEmptyException();

        if (jsonNode.has("cpf")) {
            throw new ImmutableFieldException("CPF cannot be changed after patient creation");
        }

        Patient patient = findById(id);
        if (jsonNode.has("name")) {
            String name = jsonNode.get("name").asString();
            patient.setName(name);
        }

        return patientRepository.save(patient);
    }

    public void delete(long id) {
        Patient patient = findById(id);
        patientRepository.delete(patient);
    }
}
