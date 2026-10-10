package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.DuplicateResourceException;
import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.repository.SpecialtyRepository;
import com.henrique.medical_clinic_api.util.DoctorUtil;
import com.henrique.medical_clinic_api.util.SpecialtyUtil;
import org.junit.jupiter.api.Assertions;
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

    @Test
    void findAll_ReturnListOfSpecialties_WhenSuccessful() {
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição Cardiiologista");

        Mockito.when(specialtyRepository.findAll())
                .thenReturn(List.of(specialtyMock));

        List<Specialty> all = specialtyService.findAll();

        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
        Mockito.verify(specialtyRepository, Mockito.times(1)).findAll();
    }

    @Test
    void findById_ReturnSpecialty_WhenIdIsFound() {
        long id = 1L;
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(id, "Cardiologista", "Descrição Cardiiologista");

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

    @Test
    void findDoctors_ReturnListOfDoctors_WhenSuccessful() {
        long specialtyId = 1L;
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(specialtyId, "Cardiologista", "Descrição Cardiiologista");
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Henrique", "12345", "BA", true, specialtyMock);

        Mockito.when(specialtyRepository.findById(specialtyId))
                .thenReturn(Optional.of(specialtyMock));

        List<Doctor> doctors = specialtyService.findDoctors(specialtyId);
        Doctor first = doctors.getFirst();

        Assertions.assertNotNull(doctors);
        Assertions.assertEquals(1, doctors.size());
        Assertions.assertEquals(doctorMock, first);
        Assertions.assertEquals(specialtyMock, first.getSpecialties().getFirst());
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

    @Test
    void save_SaveNewSpecialty_WhenCorrectDataIsProvided() {
        long specialtyId = 1L;
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(specialtyId, "Cardiologista", "Descrição Cardiiologista");

        Mockito.when(specialtyRepository.findByOptionalFilters(specialtyMock.getName(), null))
                .thenReturn(List.of());
        Mockito.when(specialtyRepository.save(ArgumentMatchers.any(Specialty.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Specialty savedSpecialty = specialtyService.save(specialtyMock);
        Assertions.assertNotNull(savedSpecialty);
        Assertions.assertEquals(specialtyId, savedSpecialty.getId());
        Assertions.assertEquals(specialtyMock.getDescription(), savedSpecialty.getDescription());
        Assertions.assertEquals(specialtyMock.getDoctors(), savedSpecialty.getDoctors());

        Mockito.verify(specialtyRepository, Mockito.times(1)).findByOptionalFilters(specialtyMock.getName(), null);
        Mockito.verify(specialtyRepository, Mockito.times(1)).save(specialtyMock);
    }

    @Test
    void save_ThrowsDuplicateResourceException_WhenSpecialtyAlreadyExists() {
        long specialtyId = 1L;
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(specialtyId, "Cardiologista", "Descrição Cardiiologista");

        Mockito.when(specialtyRepository.findByOptionalFilters(specialtyMock.getName(), null))
                .thenReturn(List.of(specialtyMock));

        Assertions.assertThrows(DuplicateResourceException.class, () -> specialtyService.save(specialtyMock));
        Mockito.verify(specialtyRepository, Mockito.times(1)).findByOptionalFilters(specialtyMock.getName(), null);
        Mockito.verify(specialtyRepository, Mockito.never()).save(ArgumentMatchers.any(Specialty.class));
    }

    @Test
    void updateByParts_updateSpecialty_WhenCorrectDataIsProvided() {
        long id = 1L;
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(id, "Cardiologista", "Descrição Cardiologista");
        String newDescription = "nova descrição Cardiologista";

        Mockito.when(specialtyRepository.findById(id))
                .thenReturn(Optional.of(specialtyMock));
        Mockito.when(specialtyRepository.save(ArgumentMatchers.any(Specialty.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put("description", newDescription);

        Specialty specialtyUpdated = specialtyService.updateByParts(id, jsonNode);
        Assertions.assertNotNull(specialtyUpdated);
        Assertions.assertEquals(id, specialtyUpdated.getId());
        Assertions.assertEquals(specialtyMock.getName(), specialtyUpdated.getName());
        Assertions.assertEquals(newDescription, specialtyUpdated.getDescription());

        Mockito.verify(specialtyRepository, Mockito.times(1)).findById(id);
        Mockito.verify(specialtyRepository, Mockito.times(1)).save(specialtyMock);
    }

    @Test
    void updateByParts_ThrowsResourceNotFoundException_WhenSpecialtyIsNotFound() {
        long id = 1L;

        Mockito.when(specialtyRepository.findById(id))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> specialtyService.updateByParts(id, mapper.createObjectNode()));
        Mockito.verify(specialtyRepository, Mockito.times(1)).findById(id);
        Mockito.verify(specialtyRepository, Mockito.never()).save(ArgumentMatchers.any(Specialty.class));
    }
}
