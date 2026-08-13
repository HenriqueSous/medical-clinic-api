package com.henrique.medical_clinic_api.model;

public enum Status {
    SCHEDULED("scheduled");

    private final String name;
    Status(String name) {
        this.name = name;
    }
}
