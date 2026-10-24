package com.henrique.medical_clinic_api.features.specialty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    @Query(
            "SELECT s FROM Specialty s " +
                    "WHERE (:name IS NULL OR s.name LIKE CONCAT(:name, '%'))" +
                    " AND " +
                    "(:description IS NULL OR s.description LIKE CONCAT(:description, '%'))"
    )
    List<Specialty> findByOptionalFilters(String name, String description);
}
