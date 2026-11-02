package com.henrique.medical_clinic_api.features.patient;

import com.henrique.medical_clinic_api.shared.exception.DuplicateResourceException;
import com.henrique.medical_clinic_api.shared.exception.ResourceNotFoundException;
import com.henrique.medical_clinic_api.shared.exception.BodyEmptyException;
import com.henrique.medical_clinic_api.shared.exception.ImmutableFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested
    class FindAll {
        @Test
        void findAll_ReturnListOfAllPatients_WhenSuccessful() {
            Mockito.when(patientRepository.findAll()).thenReturn(List.of(PatientUtil.createPatient()));

            List<Patient> all = patientService.findAll();

            Assertions.assertNotNull(all);
            Assertions.assertEquals(1, all.size());
        }

        @Test
        void findById_ReturnPatient_WhenIdIsFound() {
            Patient patientMock = PatientUtil.createPatient();
            long id = patientMock.getId();

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
    }

    @Nested
    class Save {
        @Test
        void save_SaveNewPatient_WhenCorrectDataIsProvided() {
            Patient patientMock = PatientUtil.createPatient();

            Mockito.when(patientRepository.findByOptionalFilters(null, patientMock.getCpf()))
                    .thenReturn(List.of());
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
            Assertions.assertEquals(patientMock.getCpf(), patient.getCpf());
        }

        @Test
        void save_ThrowsDuplicateResourceException_WhenPatientExists() {
            Patient patientMock = PatientUtil.createPatient();
            Mockito.when(patientRepository.findByOptionalFilters(null, patientMock.getCpf())).thenReturn(List.of(patientMock));

            Assertions.assertThrows(DuplicateResourceException.class, () -> patientService.save(patientMock));
            Mockito.verify(patientRepository, Mockito.never()).save(ArgumentMatchers.any(Patient.class));
        }
    }

    @Nested
    class Delete {
        @Test
        void delete_DeletePatient_WhenPatientExists() {
            Patient patientMock = PatientUtil.createPatient();
            long id = patientMock.getId();

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
    }

    @Nested
    class UpdateByParts {
        @Test
        void updateByParts_UpdatePatient_WhenCorrectDataIsProvided() {
            Patient patientMock = PatientUtil.createPatient();
            long id = patientMock.getId();

            ObjectNode jsonNode = mapper.createObjectNode();
            String newName = "Inara";
            jsonNode.put("name", newName);

            Mockito.when(patientRepository.findById(id))
                    .thenReturn(Optional.of(patientMock));
            Mockito.when(patientRepository.save(ArgumentMatchers.any(Patient.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Patient patientUpdated = patientService.updateByParts(id, jsonNode);

            Assertions.assertNotNull(patientUpdated);
            Assertions.assertEquals(id, patientUpdated.getId());
            Assertions.assertEquals(newName, patientUpdated.getName());
            Mockito.verify(patientRepository, Mockito.times(1)).save(ArgumentMatchers.any(Patient.class));
        }

        @Test
        void updateByParts_ThrowsImmutableFieldException_WhenCpfIsProvided() {
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("cpf", "94537856381");

            Assertions.assertThrows(ImmutableFieldException.class, () -> patientService.updateByParts(1L, jsonNode));
            Mockito.verify(patientRepository, Mockito.never()).save(ArgumentMatchers.any(Patient.class));
        }

        @Test
        void updateByParts_ThrowsBodyEmptyException_WhenJsonNodeIsEmpty() {
            JsonNode jsonNode = mapper.createObjectNode();

            Assertions.assertThrows(BodyEmptyException.class, () -> patientService.updateByParts(1L, jsonNode));
            Mockito.verify(patientRepository, Mockito.never()).save(ArgumentMatchers.any(Patient.class));
            Mockito.verify(patientRepository, Mockito.never()).findById(ArgumentMatchers.any(Long.class));
        }

        @Test
        void updateByParts_ThrowsResourceNotFoundException_WhenPatientNotExists() {
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("name", "Inara");

            Mockito.when(patientRepository.findById(1L))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> patientService.updateByParts(1L, jsonNode));
            Mockito.verify(patientRepository, Mockito.never()).save(ArgumentMatchers.any(Patient.class));
        }
    }
}
