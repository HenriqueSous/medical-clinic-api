package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.doctor.Doctor;
import com.henrique.medical_clinic_api.features.patient.Patient;
import com.henrique.medical_clinic_api.features.specialty.Specialty;
import com.henrique.medical_clinic_api.features.doctor.DoctorUtil;
import com.henrique.medical_clinic_api.features.patient.PatientUtil;
import com.henrique.medical_clinic_api.features.specialty.SpecialtyUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ConsultationUtil {
    public static Consultation createConsultation(long id) {
        Patient patient = PatientUtil.createPatient(1L, "Henrique", "12345678909");
        Specialty specialty = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Doctor doctor = DoctorUtil.createDoctor(1L, "Pedro", "12345", "BA", true, specialty);

        Consultation consultation = new Consultation(
                id,
                patient,
                doctor,
                LocalDate.of(2026, 9, 24),
                LocalTime.of(15, 30),
                30,
                ConsultationStatus.SCHEDULED,
                LocalDateTime.now(), LocalDateTime.now()
        );

        return consultation;
    }

    public static Consultation createConsultation(long id, LocalDateTime dateTime) {
        Patient patient = PatientUtil.createPatient(1L, "Henrique", "12345678909");
        Specialty specialty = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Doctor doctor = DoctorUtil.createDoctor(1L, "Pedro", "12345", "BA", true, specialty);

        Consultation consultation = new Consultation(
                id,
                patient,
                doctor,
                dateTime.toLocalDate(),
                dateTime.toLocalTime(),
                30,
                ConsultationStatus.SCHEDULED, LocalDateTime.now(), LocalDateTime.now()
        );

        return consultation;
    }

    public static Consultation createConsultation(long id, Patient patient, Doctor doctor, LocalDateTime consultationDataTime, int duration) {
        Consultation consultation = new Consultation(id, patient, doctor, consultationDataTime.toLocalDate(), consultationDataTime.toLocalTime(),
                duration, ConsultationStatus.SCHEDULED, LocalDateTime.now(), LocalDateTime.now());

        return consultation;
    }
}
