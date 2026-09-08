package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.repository.PatientRepository;
import com.henrique.medical_clinic_api.util.PatientUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void findAll_ReturnListOfAllPatients_WhenSuccessful() {
        Mockito.when(patientRepository.findAll()).thenReturn(PatientUtil.listOfPatients());
        List<Patient> all = patientService.findAll();

        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void findById_ReturnPatient_WhenIdIsFound() {
        long id = 1L;
        Patient patientMock = PatientUtil.createPatient(id, "Henrique", "123456");
        Mockito.when(patientRepository.findById(id)).thenReturn(Optional.of(patientMock));

        Patient patient = patientService.findById(id);

        Assertions.assertNotNull(patient);
        Assertions.assertEquals(patientMock.getId(), patient.getId());
        Assertions.assertEquals(patientMock.getName(), patient.getName());
        Assertions.assertEquals(patientMock.getCpf(), patient.getCpf());
    }

    @Test
    void findById_ThrowsResourceNotFoundException_WhenIdIsNotFound() {
        long id = 99L;
        Mockito.when(patientRepository.findById(id)).thenThrow(ResourceNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> patientService.findById(id));
    }
}
