package com.henrique.medical_clinic_api.features.doctor;

public interface DoctorDeactivationRule {
    void isDeactivationPermitted(long doctorId);
}
