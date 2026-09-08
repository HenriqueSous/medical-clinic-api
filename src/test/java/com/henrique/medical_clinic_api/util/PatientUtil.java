package com.henrique.medical_clinic_api.util;

import com.henrique.medical_clinic_api.model.Patient;

import java.time.LocalDateTime;
import java.util.List;

public class PatientUtil {
    public static List<Patient> listOfPatients() {
        return List.of(
                createPatient(1L, "Henrique", "12345")
        );
    }

    public static Patient createPatient(long id, String name, String cpf) {
        return new Patient(id, name, cpf, LocalDateTime.now(), LocalDateTime.now(), List.of());
    }
}
