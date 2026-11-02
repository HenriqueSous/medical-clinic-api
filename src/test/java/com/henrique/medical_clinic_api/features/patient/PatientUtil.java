package com.henrique.medical_clinic_api.features.patient;

import java.time.LocalDateTime;

public class PatientUtil {
    public static Patient createPatient(long id, String name, String cpf) {
        return new Patient(id, name, cpf, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Patient createPatient() {
        return new Patient(1L, "Henrique", "12345678987", LocalDateTime.now(), LocalDateTime.now());
    }
}
