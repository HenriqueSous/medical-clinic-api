package com.henrique.medical_clinic_api.features.patient;

public interface PatientDeactivationRule {
    void isDeactivationPermitted(long patientId);
}
