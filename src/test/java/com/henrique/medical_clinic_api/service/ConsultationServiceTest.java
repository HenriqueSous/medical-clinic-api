package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.model.Consultation;
import com.henrique.medical_clinic_api.repository.ConsultationRepository;
import com.henrique.medical_clinic_api.util.ConsultationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {
    @InjectMocks
    private ConsultationService consultationService;

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private DoctorService doctorService;

    @Mock
    private PatientService patientService;

    @Test
    void findById_ReturnConsultation_WhenConsultationExists() {
        Consultation consultationMock = ConsultationUtil.createConsultation(1L);

        Mockito.when(consultationRepository.findById(consultationMock.getId()))
                .thenReturn(Optional.of(consultationMock));

        Consultation consultationFound = consultationService.findById(1L);

        Assertions.assertNotNull(consultationFound);
        Assertions.assertEquals(consultationMock, consultationFound);
    }

    @Test
    void findById_ThrowsResourceNotFoundException_WhenConsultationNotExists() {
        Consultation consultationMock = ConsultationUtil.createConsultation(1L);

        Mockito.when(consultationRepository.findById(consultationMock.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> consultationService.findById(1L));
    }
}