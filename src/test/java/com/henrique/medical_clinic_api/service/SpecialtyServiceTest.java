package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.repository.SpecialtyRepository;
import com.henrique.medical_clinic_api.util.DoctorUtil;
import com.henrique.medical_clinic_api.util.SpecialtyUtil;
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
class SpecialtyServiceTest {
    @InjectMocks
    private SpecialtyService specialtyService;

    @Mock
    private SpecialtyRepository specialtyRepository;

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
}
