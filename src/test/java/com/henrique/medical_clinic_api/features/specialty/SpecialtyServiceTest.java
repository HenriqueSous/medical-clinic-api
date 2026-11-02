package com.henrique.medical_clinic_api.features.specialty;

import com.henrique.medical_clinic_api.features.doctor.Doctor;
import com.henrique.medical_clinic_api.features.doctor.DoctorUtil;
import com.henrique.medical_clinic_api.shared.exception.BodyEmptyException;
import com.henrique.medical_clinic_api.shared.exception.DuplicateResourceException;
import com.henrique.medical_clinic_api.shared.exception.ImmutableFieldException;
import com.henrique.medical_clinic_api.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {
    @InjectMocks
    private SpecialtyService specialtyService;

    @Mock
    private SpecialtyRepository specialtyRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested
    class FindAll {
        @Test
        void findAll_ReturnListOfSpecialties_WhenSuccessful() {
            Specialty specialtyMock = SpecialtyUtil.createSpecialty();

            Mockito.when(specialtyRepository.findAll())
                    .thenReturn(List.of(specialtyMock));

            List<Specialty> all = specialtyService.findAll();

            Assertions.assertNotNull(all);
            Assertions.assertEquals(1, all.size());
            Mockito.verify(specialtyRepository, Mockito.times(1)).findAll();
        }
    }

    @Nested
    class FindById {
        @Test
        void findById_ReturnSpecialty_WhenIdIsFound() {
            Specialty specialtyMock = SpecialtyUtil.createSpecialty();
            long id = specialtyMock.getId();

            Mockito.when(specialtyRepository.findById(id))
                    .thenReturn(Optional.of(specialtyMock));

            Specialty specialtyFound = specialtyService.findById(id);

            Assertions.assertNotNull(specialtyFound);
            Assertions.assertEquals(id, specialtyFound.getId());
            Assertions.assertEquals(specialtyMock.getName(), specialtyFound.getName());
            Assertions.assertEquals(specialtyMock.getDescription(), specialtyFound.getDescription());
            Assertions.assertEquals(specialtyMock.getDoctors(), specialtyFound.getDoctors());
            Mockito.verify(specialtyRepository, Mockito.times(1)).findById(id);
        }

        @Test
        void findById_ThrowsResourceNotFoundException_WhenIdIsNotFound() {
            long id = 1L;

            Mockito.when(specialtyRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> specialtyService.findById(id));
            Mockito.verify(specialtyRepository, Mockito.times(1)).findById(id);
        }
    }

    @Nested
    class FindDoctors {
        @Test
        void findDoctors_ReturnListOfDoctors_WhenSuccessful() {
            Specialty specialtyMock = SpecialtyUtil.createSpecialty();
            long specialtyId = specialtyMock.getId();
            Doctor doctorMock = DoctorUtil.createDoctor(true, specialtyMock);

            Mockito.when(specialtyRepository.findById(specialtyId))
                    .thenReturn(Optional.of(specialtyMock));

            List<Doctor> doctors = specialtyService.findDoctors(specialtyId);

            Assertions.assertNotNull(doctors);
            Assertions.assertEquals(1, doctors.size());

            Doctor first = doctors.getFirst();
            Assertions.assertSame(doctorMock, first);
            Assertions.assertSame(specialtyMock, first.getSpecialties().getFirst());

            Mockito.verify(specialtyRepository, Mockito.times(1)).findById(specialtyId);
        }

        @Test
        void findDoctors_ThrowsResourceNotFoundException_WhenIdIsNotFound() {
            long specialtyId = 1L;

            Mockito.when(specialtyRepository.findById(specialtyId))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> specialtyService.findDoctors(specialtyId));
            Mockito.verify(specialtyRepository, Mockito.times(1)).findById(specialtyId);
        }
    }

    @Nested
    class Save {
        @Test
        void save_SaveNewSpecialty_WhenCorrectDataIsProvided() {
            Specialty specialtyMock = SpecialtyUtil.createSpecialty();

            Mockito.when(specialtyRepository.findByOptionalFilters(specialtyMock.getName(), null))
                    .thenReturn(List.of());
            Mockito.when(specialtyRepository.save(ArgumentMatchers.any(Specialty.class)))
                    .thenAnswer(
                            inv -> {
                                Specialty specialty = inv.getArgument(0);
                                specialty.setId(10L);
                                return specialty;
                            }
                    );

            Specialty savedSpecialty = specialtyService.save(specialtyMock);
            Assertions.assertNotNull(savedSpecialty);
            Assertions.assertEquals(specialtyMock.getDescription(), savedSpecialty.getDescription());
            Assertions.assertEquals(specialtyMock.getDoctors(), savedSpecialty.getDoctors());

            Mockito.verify(specialtyRepository, Mockito.times(1)).save(specialtyMock);
        }

        @Test
        void save_ThrowsDuplicateResourceException_WhenSpecialtyAlreadyExists() {
            Specialty specialtyMock = SpecialtyUtil.createSpecialty();

            Mockito.when(specialtyRepository.findByOptionalFilters(specialtyMock.getName(), null))
                    .thenReturn(List.of(specialtyMock));

            Assertions.assertThrows(DuplicateResourceException.class, () -> specialtyService.save(specialtyMock));
            Mockito.verify(specialtyRepository, Mockito.times(1)).findByOptionalFilters(specialtyMock.getName(), null);
            Mockito.verify(specialtyRepository, Mockito.never()).save(ArgumentMatchers.any(Specialty.class));
        }
    }

    @Nested
    class UpdateByParts {
        private ObjectNode jsonNode;

        @BeforeEach
        void setup() {
            jsonNode = mapper.createObjectNode();
        }

        @Test
        void updateByParts_updateSpecialty_WhenCorrectDataIsProvided() {
            Specialty specialtyMock = SpecialtyUtil.createSpecialty();
            long id = specialtyMock.getId();
            String newDescription = "nova descrição";

            Mockito.when(specialtyRepository.findById(id))
                    .thenReturn(Optional.of(specialtyMock));
            Mockito.when(specialtyRepository.save(ArgumentMatchers.any(Specialty.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            jsonNode.put("description", newDescription);
            Specialty specialtyUpdated = specialtyService.updateByParts(id, jsonNode);

            Assertions.assertNotNull(specialtyUpdated);
            Assertions.assertEquals(id, specialtyUpdated.getId());
            Assertions.assertEquals(specialtyMock.getName(), specialtyUpdated.getName());
            Assertions.assertEquals(newDescription, specialtyUpdated.getDescription());

            Mockito.verify(specialtyRepository, Mockito.times(1)).save(specialtyMock);
        }

        @Test
        void updateByParts_ThrowsImmutableFieldException_WhenNameIsProvided() {
            Specialty specialtyMock = SpecialtyUtil.createSpecialty();
            long id = specialtyMock.getId();

            Mockito.when(specialtyRepository.findById(id))
                    .thenReturn(Optional.of(specialtyMock));

            jsonNode.put("name", "Neuro");

            Assertions.assertThrows(ImmutableFieldException.class, () -> specialtyService.updateByParts(id, jsonNode));

            Mockito.verify(specialtyRepository, Mockito.never()).save(ArgumentMatchers.any(Specialty.class));
        }

        @Test
        void updateByParts_ThrowsBodyEmptyException_WhenBodyIsEmpty() {
            Assertions.assertThrows(BodyEmptyException.class, () -> specialtyService.updateByParts(1L, mapper.createObjectNode()));

            Mockito.verify(specialtyRepository, Mockito.never()).save(ArgumentMatchers.any(Specialty.class));
        }

        @Test
        void updateByParts_ThrowsResourceNotFoundException_WhenSpecialtyIsNotFound() {
            long id = 1L;

            Mockito.when(specialtyRepository.findById(id))
                    .thenReturn(Optional.empty());

            jsonNode.put("description", "test");

            Assertions.assertThrows(ResourceNotFoundException.class, () -> specialtyService.updateByParts(id, jsonNode));
            Mockito.verify(specialtyRepository, Mockito.never()).save(ArgumentMatchers.any(Specialty.class));
        }
    }

    @Nested
    class Delete {
        @Test
        void delete_DeleteSpecialty_WhenSpecialtyIsFound() {
            Specialty specialtyMock = SpecialtyUtil.createSpecialty();
            long id = specialtyMock.getId();

            Mockito.when(specialtyRepository.findById(id))
                    .thenReturn(Optional.of(specialtyMock));
            Mockito.doNothing()
                    .when(specialtyRepository).delete(specialtyMock);

            specialtyService.delete(id);
            Mockito.verify(specialtyRepository, Mockito.times(1)).delete(specialtyMock);
        }

        @Test
        void delete_ThrowsResourceNotFoundException_WhenSpecialtyIsNotFound() {
            long id = 1L;

            Mockito.when(specialtyRepository.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> specialtyService.delete(id));
            Mockito.verify(specialtyRepository, Mockito.never()).delete(ArgumentMatchers.any(Specialty.class));
        }
    }
}
