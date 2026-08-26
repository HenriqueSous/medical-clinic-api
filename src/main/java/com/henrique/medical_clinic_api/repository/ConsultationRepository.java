package com.henrique.medical_clinic_api.repository;

import com.henrique.medical_clinic_api.model.Consultation;
import com.henrique.medical_clinic_api.model.ConsultationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    @Query(
            "SELECT c FROM Consultation c " +
                    "WHERE (:patientId IS NULL OR c.patient.id = :patientId)" +
                    " AND " +
                    "(:doctorId IS NULL OR c.doctor.id = :doctorId)" +
                    " AND " +
                    "(:consultationDate IS NULL OR c.consultationDate = :consultationDate)" +
                    " AND " +
                    "(:consultationTime IS NULL OR c.consultationTime = :consultationTime)" +
                    " AND " +
                    "(:status IS NULL OR c.status = :status)" +
                    " AND " +
                    "(:duration IS NULL OR c.duration = :duration)"
    )
    List<Consultation> findByOptionalFilters(
            Long patientId,
            Long doctorId,
            LocalDate consultationDate,
            LocalTime consultationTime,
            ConsultationStatus status,
            Integer duration
    );
}
