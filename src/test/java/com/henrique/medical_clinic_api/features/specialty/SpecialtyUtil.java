package com.henrique.medical_clinic_api.features.specialty;

import java.util.ArrayList;

public class SpecialtyUtil {
    public static Specialty createSpecialty(long id, String name, String description) {
        return new Specialty(id, name, description, new ArrayList<>());
    }
}
