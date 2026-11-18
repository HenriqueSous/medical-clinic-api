package com.henrique.medical_clinic_api.shared.domain;

public enum ProfileStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String name;
    ProfileStatus(String name) {
        this.name = name;
    }
}
