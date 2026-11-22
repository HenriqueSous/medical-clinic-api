package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.doctor.Doctor;
import com.henrique.medical_clinic_api.features.doctor.DoctorUtil;
import com.henrique.medical_clinic_api.features.patient.Patient;
import com.henrique.medical_clinic_api.features.patient.PatientUtil;
import com.henrique.medical_clinic_api.features.specialty.SpecialtyUtil;
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
class ProfileDeactivationRuleImplTest {
    @InjectMocks
    private ProfileDeactivationRuleImpl profileDeactivationRule;

    @Mock
    private ConsultationRepository consultationRepository;

    private static Doctor doctorMock;

    private static Patient patientMock;

    @BeforeAll
    static void setup() {
        doctorMock = DoctorUtil.createDoctor(
                1L, "Pedro", "12345", "BA", true,
                SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista")
        );
        patientMock = PatientUtil.createPatient();
    }

    @Test
    void isDoctorDeactivationPermitted_DoesNotThrowException_WhenDoctorHasNotPendingAppointments() {
        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of());

        Assertions.assertDoesNotThrow(() -> profileDeactivationRule.isDoctorDeactivationPermitted(doctorMock.getId()));
    }

    @ParameterizedTest
    @EnumSource(value = ConsultationStatus.class, names = {"SCHEDULED", "CONFIRMED", "ON_HOLD", "BEING_SERVED"})
    void isDoctorDeactivationPermitted_ThrowsPendingConsultationsException_WhenDoctorHasPendingAppointments(ConsultationStatus status) {
        Consultation consultationMock = ConsultationUtil.createConsultation(1L, doctorMock);
        consultationMock.setStatus(status);

        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of(consultationMock));

        Assertions.assertThrows(PendingConsultationsException.class, () -> profileDeactivationRule.isDoctorDeactivationPermitted(doctorMock.getId()));
    }

    @Test
    void isPatietnDeactivationPermitted_DoesNotThrowException_WhenPatientHasNotPendingAppointments() {
        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of());

        Assertions.assertDoesNotThrow(() -> profileDeactivationRule.isPatientDeactivationPermitted(patientMock.getId()));
    }

    @ParameterizedTest
    @EnumSource(value = ConsultationStatus.class, names = {"SCHEDULED", "CONFIRMED", "ON_HOLD", "BEING_SERVED"})
    void isPatientDeactivationPermitted_ThrowsPendingConsultationsException_WhenPatientHasPendingAppointments(ConsultationStatus status) {
        Consultation consultationMock = ConsultationUtil.createConsultation(1L, patientMock);
        consultationMock.setStatus(status);

        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of(consultationMock));

        Assertions.assertThrows(PendingConsultationsException.class, () -> profileDeactivationRule.isPatientDeactivationPermitted(patientMock.getId()));
    }
}
