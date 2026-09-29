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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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

        Patient patient = patientService.save(patientMock);

        Assertions.assertNotNull(patient);
        Assertions.assertEquals(patientMock.getName(), patient.getName());
        Assertions.assertEquals(10L, patient.getId());
        Assertions.assertEquals(patientMock.getCpf(), patient.getCpf());
    }

    @Test
    void save_ThrowsDuplicateResourceException_WhenPatientExists() {
        Patient patientMock = PatientUtil.createPatient(1, "Henrique", "12345678987");
        Mockito.when(patientRepository.findByOptionalFilters(null, patientMock.getCpf())).thenReturn(List.of(patientMock));

        Assertions.assertThrows(DuplicateResourceException.class, () -> patientService.save(patientMock));
        Mockito.verify(patientRepository, Mockito.never()).save(ArgumentMatchers.any(Patient.class));
    }

    @Test
    void delete_DeletePatient_WhenPatientExists() {
        long id = 1L;
        Patient patientMock = PatientUtil.createPatient(id, "Henrique", "12345678987");

        Mockito.when(patientRepository.findById(id))
                .thenReturn(Optional.of(patientMock));

        patientService.delete(id);
        Mockito.verify(patientRepository, Mockito.times(1)).delete(patientMock);
        Mockito.verify(patientRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void delete_ThrowsResourceNotFoundException_WhenPatientNotExists() {
        long id = 1L;

        Mockito.when(patientRepository.findById(id))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> patientService.delete(id));
        Mockito.verify(patientRepository, Mockito.never()).delete(ArgumentMatchers.any(Patient.class));
        Mockito.verify(patientRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void updateByParts_UpdatePatient_WhenCorrectDataIsProvided() {
        Patient patientMock = PatientUtil.createPatient(1L, "Henrique", "12345678987");
        Patient update = PatientUtil.createPatient(1L, "Inara", "09876543245");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.valueToTree(update);

        Mockito.when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patientMock));
        Mockito.when(patientRepository.save(ArgumentMatchers.any(Patient.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Patient patientUpdated = patientService.updateByParts(1L, jsonNode);

        Assertions.assertNotNull(patientUpdated);
        Assertions.assertEquals(update.getId(), patientUpdated.getId());
        Assertions.assertEquals(update.getName(), patientUpdated.getName());
        Assertions.assertEquals(update.getCpf(), patientUpdated.getCpf());

        Mockito.verify(patientRepository, Mockito.times(1)).save(ArgumentMatchers.any(Patient.class));
    }
}
