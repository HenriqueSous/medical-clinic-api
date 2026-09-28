package com.henrique.medical_clinic_api.util;

import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DoctorUtil {
    public static Doctor createDoctor(long id, String name, String crm, String uf) {
        return new Doctor(id, name, crm, uf, LocalDateTime.now(), LocalDateTime.now(), List.of(), List.of());
    }

    public static Doctor createDoctor(long id, String name, String crm, String uf, Specialty... specialties) {
        return new Doctor(id, name, crm, uf, LocalDateTime.now(), LocalDateTime.now(), List.of(), Arrays.asList(specialties));
    }

    public static List<Doctor> listOfDoctors() {
        return List.of(
                createDoctor(1, "Henrique", "12345", "BA")
        );
    }
}
