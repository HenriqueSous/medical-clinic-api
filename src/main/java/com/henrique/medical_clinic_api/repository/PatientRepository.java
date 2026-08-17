package com.henrique.medical_clinic_api.repository;

import com.henrique.medical_clinic_api.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
