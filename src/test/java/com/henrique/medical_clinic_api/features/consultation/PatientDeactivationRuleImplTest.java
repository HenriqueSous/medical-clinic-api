package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.patient.Patient;
import com.henrique.medical_clinic_api.features.patient.PatientUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PatientDeactivationRuleImplTest {

    @InjectMocks
    private PatientDeactivationRuleImpl patientDeactivationRule;

    @Mock
    private ConsultationRepository consultationRepository;

    private static Patient patientMock;

    @BeforeAll
    static void setup() {
        patientMock = PatientUtil.createPatient();
    }

    @Test
    void isDeactivationPermitted_DoesNotThrowException_WhenPatientHasNotPendingAppointments() {
        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of());

        Assertions.assertDoesNotThrow(() -> patientDeactivationRule.isDeactivationPermitted(patientMock.getId()));
    }

    @ParameterizedTest
    @EnumSource(value = ConsultationStatus.class, names = {"SCHEDULED", "CONFIRMED", "ON_HOLD", "BEING_SERVED"})
    void isDeactivationPermitted_ThrowsPendingConsultationsException_WhenPatientHasPendingAppointments(ConsultationStatus status) {
        Consultation consultationMock = ConsultationUtil.createConsultation(1L, patientMock);
        consultationMock.setStatus(status);

        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of(consultationMock));

        Assertions.assertThrows(PendingConsultationsException.class, () -> patientDeactivationRule.isDeactivationPermitted(patientMock.getId()));
    }
}
