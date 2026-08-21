package com.henrique.medical_clinic_api.repository;

import com.henrique.medical_clinic_api.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query(
            "SELECT d FROM Doctor d " +
                    "WHERE (:name IS NULL OR d.name LIKE CONCAT(:name, '%'))" +
                    " AND " +
                    "(:crm IS NULL OR d.crm LIKE CONCAT(:crm, '%'))" +
                    " AND " +
                    "(:uf IS NULL OR d.uf LIKE CONCAT(:uf, '%'))"
    )
    List<Doctor> findByOptionalFilters(String name, String crm, String uf);
}
