package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.doctor.Doctor;
import com.henrique.medical_clinic_api.features.doctor.DoctorUtil;
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
class DoctorDeactivationRuleImplTest {
    @InjectMocks
    private DoctorDeactivationRuleImpl doctorDeactivationRule;

    @Mock
    private ConsultationRepository consultationRepository;

    private static Doctor doctorMock;

    @BeforeAll
    static void setup() {
        doctorMock = DoctorUtil.createDoctor(
                1L, "Pedro", "12345", "BA", true,
                SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista")
        );
    }

    @Test
    void isDeactivationPermitted_DoesNotThrowException_WhenDoctorHasNotPendingAppointments() {
        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of());

        Assertions.assertDoesNotThrow(() -> doctorDeactivationRule.isDeactivationPermitted(doctorMock.getId()));
    }

    @ParameterizedTest
    @EnumSource(value = ConsultationStatus.class, names = {"SCHEDULED", "CONFIRMED", "ON_HOLD", "BEING_SERVED"})
    void isDeactivationPermitted_ThrowsPendingConsultationsException_WhenDoctorHasPendingAppointments(ConsultationStatus status) {
        Consultation consultationMock = ConsultationUtil.createConsultation(1L, doctorMock);
        consultationMock.setStatus(status);

        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of(consultationMock));

        Assertions.assertThrows(PendingConsultationsException.class, () -> doctorDeactivationRule.isDeactivationPermitted(doctorMock.getId()));
    }
}
