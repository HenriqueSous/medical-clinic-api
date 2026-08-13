package com.henrique.medical_clinic_api.model;

import jakarta.persistence.*;

import java.util.List;

public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    @ManyToMany(mappedBy = "specialties", cascade = CascadeType.MERGE)
    private List<Doctor> doctors;
}
