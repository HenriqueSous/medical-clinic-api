package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.DuplicateResourceException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.queryFilters.DoctorQueryFilter;
import com.henrique.medical_clinic_api.repository.DoctorRepository;
import com.henrique.medical_clinic_api.util.DoctorUtil;
import com.henrique.medical_clinic_api.util.PatientUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void findAll_ReturnListOfAllDoctors_WhenSuccessful() {
        Mockito.when(doctorRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(DoctorUtil.listOfDoctors());

        List<Doctor> doctors = doctorService.find(new DoctorQueryFilter(null, null, null));

        Assertions.assertNotNull(doctors);
        Assertions.assertEquals(1, doctors.size());
    }

    @Test
    void findById_ReturnDoctor_WhenIdIsFound() {
        long id = 1L;
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Henrique", "12345", "BA");
        Mockito.when(doctorRepository.findById(id)).thenReturn(Optional.of(doctorMock));

        Doctor doctor = doctorService.findById(id);

        Assertions.assertNotNull(doctor);
        Assertions.assertEquals(doctorMock.getId(), doctor.getId());
        Assertions.assertEquals(doctorMock.getName(), doctor.getName());
        Assertions.assertEquals(doctorMock.getCrm(), doctor.getCrm());
        Assertions.assertEquals(doctorMock.getUf(), doctor.getUf());
    }

    @Test
    void save_ThrowsDuplicateResourceException_WhenDoctorExists() {
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Henrique", "12345", "BA");

        // Fix the gap caused by any() in the integration tests
        Mockito.when(doctorRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of(doctorMock));

        Assertions.assertThrows(DuplicateResourceException.class, () -> doctorService.save(doctorMock));
    }
}
