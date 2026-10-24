package com.henrique.medical_clinic_api.features.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query(
            "SELECT p FROM Patient p " +
                    "WHERE (:name IS NULL OR p.name LIKE CONCAT(:name, '%'))" +
                    " AND " +
                    "(:cpf IS NULL OR p.cpf LIKE CONCAT(:cpf, '%'))"
    )
    List<Patient> findByOptionalFilters(String name, String cpf);
}
