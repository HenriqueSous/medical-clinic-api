package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.shared.domain.ProfileDeactivationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileDeactivationRuleImpl implements ProfileDeactivationRule {

    @Autowired
    private ConsultationRepository repository;

    @Override
    public void isDoctorDeactivationPermitted(long doctorId) {
        List<Consultation> consultations = repository.findAll(
                ConsultationSpecifications.equalsDoctorId(doctorId)
                        .and(ConsultationSpecifications.notEqualsConsultationStatus(ConsultationStatus.CARRIED_OUT))
                        .and(ConsultationSpecifications.notEqualsConsultationStatus(ConsultationStatus.CANCELED))
        );

        if (!consultations.isEmpty()) {
            throw new PendingConsultationsException("The doctor with id "+doctorId+" still has pending appointments");
        }
    }

    @Override
    public void isPatientDeactivationPermitted(long patientId) {
        List<Consultation> consultations = repository.findAll(
                ConsultationSpecifications.equalsPatientId(patientId)
                        .and(ConsultationSpecifications.notEqualsConsultationStatus(ConsultationStatus.CARRIED_OUT))
                        .and(ConsultationSpecifications.notEqualsConsultationStatus(ConsultationStatus.CANCELED))
        );

        if (!consultations.isEmpty()) {
            throw new PendingConsultationsException("The patient with id "+patientId+" still has pending appointments");
        }
    }
}
