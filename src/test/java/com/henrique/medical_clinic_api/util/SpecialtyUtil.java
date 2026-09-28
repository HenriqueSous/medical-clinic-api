package com.henrique.medical_clinic_api.util;

import com.henrique.medical_clinic_api.model.Specialty;

import java.util.List;

public class SpecialtyUtil {
    public static Specialty createSpecialty(long id, String name, String description) {
        return new Specialty(id, name, description, List.of());
    }
}
