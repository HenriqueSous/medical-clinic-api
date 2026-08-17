package com.henrique.medical_clinic_api.model;

public enum Status {
    SCHEDULED("scheduled"),
    CONFIRMED("confirmed"),
    ON_HOLD("on hold"),
    BEING_SERVED("being served"),
    CARRIED_OUT("carried out"),
    CANCELED("canceled");

    private final String name;
    Status(String name) {
        this.name = name;
    }
}
