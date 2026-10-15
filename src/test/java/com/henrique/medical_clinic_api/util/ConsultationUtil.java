package com.henrique.medical_clinic_api.util;

import com.henrique.medical_clinic_api.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ConsultationUtil {
    public static Consultation createConsultation(long id, boolean linkPatientDoctor) {
        Patient patient = PatientUtil.createPatient(1L, "Henrique", "12345678909");
        Specialty specialty = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Doctor doctor = DoctorUtil.createDoctor(1L, "Pedro", "12345", "BA", true, specialty);

        Consultation consultation = new Consultation(id, patient, doctor, LocalDate.of(2026, 9, 24),
                LocalTime.of(15, 30), 30, ConsultationStatus.SCHEDULED, LocalDateTime.now(), LocalDateTime.now());

        if (linkPatientDoctor) {
            linkPatientDoctorToConsultation(patient, doctor, consultation);
        }

        return consultation;
    }

    public static Consultation createConsultation(long id, Patient patient, Doctor doctor, LocalDateTime consultationDataTime, boolean linkPatientDoctor) {
        Consultation consultation = new Consultation(id, patient, doctor, consultationDataTime.toLocalDate(), consultationDataTime.toLocalTime(),
                30, ConsultationStatus.SCHEDULED, LocalDateTime.now(), LocalDateTime.now());

        if (linkPatientDoctor) {
            linkPatientDoctorToConsultation(patient, doctor, consultation);
        }

        return consultation;
    }

    public static void setDoctorConsultations(Doctor doctor) {
        Patient patient1 = PatientUtil.createPatient(2L, "Inara", "09876543212");

        doctor.getConsultations().add(
            createConsultation(1L, patient1, doctor, LocalDateTime.of(2026, 9, 24, 16, 10), true)
        );
    }

    public static void linkPatientDoctorToConsultation(Patient patient, Doctor doctor, Consultation consultation) {
        patient.getConsultations().add(consultation);
        doctor.getConsultations().add(consultation);
    }
}
