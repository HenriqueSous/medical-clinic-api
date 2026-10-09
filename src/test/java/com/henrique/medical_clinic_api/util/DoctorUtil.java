package com.henrique.medical_clinic_api.util;

import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoctorUtil {
    public static Doctor createDoctor(long id, String name, String crm, String uf) {
        return new Doctor(id, name, crm, uf, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), List.of());
    }

    public static Doctor createDoctor(long id, String name, String crm, String uf, boolean linkSpecialties, Specialty... specialties) {
        Doctor doctor = new Doctor(id, name, crm, uf, LocalDateTime.now(), LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>());

        if (linkSpecialties) {
            linkDoctorToSpecialties(doctor, specialties);
        } else {
            doctor.setSpecialties(Arrays.asList(specialties));
        }

        return doctor;
    }

    public static List<Doctor> listOfDoctors() {
        return List.of(
                createDoctor(1, "Henrique", "12345", "BA")
        );
    }

    private static void linkDoctorToSpecialties(Doctor doctor, Specialty... specialties) {
        List<Specialty> doctorSpecialties = doctor.getSpecialties();

        for (Specialty specialty : specialties) {
            List<Doctor> specialtyDoctors = specialty.getDoctors();

            if (!doctorSpecialties.contains(specialty)) {
                doctorSpecialties.add(specialty);
            }
            if (!specialtyDoctors.contains(doctor)) {
                specialtyDoctors.add(doctor);
                specialty.setDoctors(specialtyDoctors);
            }
        }
        doctor.setSpecialties(doctorSpecialties);
    }
}
