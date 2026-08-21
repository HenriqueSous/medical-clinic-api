package com.henrique.medical_clinic_api.repository;

import com.henrique.medical_clinic_api.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    Specialty findByNameIgnoreCase(String name);
}
