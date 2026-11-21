package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.doctor.DoctorDeactivationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorDeactivationRuleImpl implements DoctorDeactivationRule {
    @Autowired
    private ConsultationRepository repository;

    @Override
    public void isDeactivationPermitted(long doctorId) {
        List<Consultation> consultations = repository.findAll(
                ConsultationSpecifications.equalsDoctorId(doctorId)
                        .and(ConsultationSpecifications.notEqualsConsultationStatus(ConsultationStatus.CARRIED_OUT))
                        .and(ConsultationSpecifications.notEqualsConsultationStatus(ConsultationStatus.CANCELED))
        );

        if (!consultations.isEmpty()) {
            throw new PendingConsultationsException("The doctor with id "+doctorId+" still has pending appointments");
        }
    }
}
