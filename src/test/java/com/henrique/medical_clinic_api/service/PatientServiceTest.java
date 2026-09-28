package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.DuplicateResourceException;
import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.repository.PatientRepository;
import com.henrique.medical_clinic_api.util.PatientUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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
        Patient patientMock = PatientUtil.createPatient(id, "Henrique", "12345678987");
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
        Mockito.when(patientRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> patientService.findById(id));
    }

    @Test
    void save_SaveNewPatient_WhenCorrectDataIsProvided() {
        Patient patientMock = PatientUtil.createPatient(1, "Henrique", "12345678987");
        Mockito.when(patientRepository.save(ArgumentMatchers.any(Patient.class)))
                .thenAnswer(
                        inv -> {
                            Patient p = inv.getArgument(0);
                            p.setId(10L);
                            return p;
                        }
                );

        Patient patient = patientService.savePatient(patientMock);

        Assertions.assertNotNull(patient);
        Assertions.assertEquals(patientMock.getName(), patient.getName());
        Assertions.assertEquals(10L, patient.getId());
        Assertions.assertEquals(patientMock.getCpf(), patient.getCpf());
    }

    @Test
    void save_ThrowsDuplicateResourceException_WhenPatientExists() {
        Patient patientMock = PatientUtil.createPatient(1, "Henrique", "12345678987");
        Mockito.when(patientRepository.findByOptionalFilters(null, patientMock.getCpf())).thenReturn(List.of(patientMock));

        Assertions.assertThrows(DuplicateResourceException.class, () -> patientService.savePatient(patientMock));
    }
}
