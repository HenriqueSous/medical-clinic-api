package com.henrique.medical_clinic_api.features.specialty;

import java.util.ArrayList;

public class SpecialtyUtil {
    public static Specialty createSpecialty(long id, String name, String description) {
        return new Specialty(id, name, description, new ArrayList<>());
    }

    public static Specialty createSpecialty() {
        return new Specialty(1L, "Clínico Geral", "Descrição Clínico Geral", new ArrayList<>());
    }
}
