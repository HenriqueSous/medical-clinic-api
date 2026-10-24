package com.henrique.medical_clinic_api.util;

import com.henrique.medical_clinic_api.features.specialty.Specialty;

import java.util.ArrayList;

public class SpecialtyUtil {
    public static Specialty createSpecialty(long id, String name, String description) {
        return new Specialty(id, name, description, new ArrayList<>());
    }
}
