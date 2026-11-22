package com.henrique.medical_clinic_api.shared.domain;

public interface ProfileDeactivationRule {
    void isDoctorDeactivationPermitted(long doctorId);
    void isPatientDeactivationPermitted(long patientId);
}
