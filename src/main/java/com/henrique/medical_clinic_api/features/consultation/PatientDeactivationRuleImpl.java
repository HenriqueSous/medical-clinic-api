package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.patient.PatientDeactivationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientDeactivationRuleImpl implements PatientDeactivationRule {
    @Autowired
    private ConsultationRepository repository;

    @Override
    public void isDeactivationPermitted(long patientId) {
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
