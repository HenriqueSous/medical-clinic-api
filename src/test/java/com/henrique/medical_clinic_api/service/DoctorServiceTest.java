package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.exception.resource.DuplicateResourceException;
import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Specialty;
import com.henrique.medical_clinic_api.queryFilters.DoctorQueryFilter;
import com.henrique.medical_clinic_api.repository.DoctorRepository;
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
import org.springframework.data.jpa.domain.Specification;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialtyService specialtyService;

    @InjectMocks
    private DoctorService doctorService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void findAll_ReturnListOfAllDoctors_WhenSuccessful() {
        Mockito.when(doctorRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(DoctorUtil.listOfDoctors());

        List<Doctor> doctors = doctorService.find(new DoctorQueryFilter(null, null, null));

        Assertions.assertNotNull(doctors);
        Assertions.assertEquals(1, doctors.size());
        Mockito.verify(doctorRepository, Mockito.times(1)).findAll(ArgumentMatchers.any(Specification.class));
    }

    @Test
    void findById_ReturnDoctor_WhenIdIsFound() {
        long id = 1L;
        Doctor doctorMock = DoctorUtil.createDoctor(id, "Henrique", "12345", "BA");
        Mockito.when(doctorRepository.findById(id)).thenReturn(Optional.of(doctorMock));

        Doctor doctor = doctorService.findById(id);

        Assertions.assertNotNull(doctor);
        Assertions.assertEquals(doctorMock.getId(), doctor.getId());
        Assertions.assertEquals(doctorMock.getName(), doctor.getName());
        Assertions.assertEquals(doctorMock.getCrm(), doctor.getCrm());
        Assertions.assertEquals(doctorMock.getUf(), doctor.getUf());
        Mockito.verify(doctorRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void findById_ThrowsResourceNotFoundException_WhenIdIsNotFound() {
        long id = 99L;
        Mockito.when(doctorRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> doctorService.findById(id));
        Mockito.verify(doctorRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void save_SaveNewDoctor_WhenCorrectDataIsProvided() {
        Specialty specialty = SpecialtyUtil.createSpecialty(1L, "Clínico Geral", "Descrição");
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Henrique", "12345", "BA", specialty);

        Mockito.when(doctorRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of());
        Mockito.when(specialtyService.findByOptionalFilters(specialty.getName(), null))
                .thenReturn(List.of());
        Mockito.when(doctorRepository.save(doctorMock))
                .thenAnswer(inv -> inv.getArgument(0));

        Doctor doctorSaved = doctorService.save(doctorMock);
        Specialty specialtySaved = doctorMock.getSpecialties().getFirst();

        Assertions.assertNotNull(doctorSaved);
        Assertions.assertEquals(doctorMock.getId(), doctorSaved.getId());
        Assertions.assertEquals(doctorMock.getName(), doctorSaved.getName());
        Assertions.assertEquals(doctorMock.getCrm(), doctorSaved.getCrm());
        Assertions.assertEquals(doctorMock.getUf(), doctorSaved.getUf());
        Assertions.assertEquals(doctorMock.getSpecialties(), doctorSaved.getSpecialties());
        Assertions.assertEquals(1, specialtySaved.getDoctors().size());
        Assertions.assertTrue(specialtySaved.getDoctors().contains(doctorMock));
        Mockito.verify(doctorRepository, Mockito.times(1)).findAll(ArgumentMatchers.any(Specification.class));
        Mockito.verify(doctorRepository, Mockito.times(1)).save(doctorMock);
    }

    @Test
    void save_ThrowsDuplicateResourceException_WhenDoctorExists() {
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Henrique", "12345", "BA");

        // Fix the gap caused by any() in the integration tests
        Mockito.when(doctorRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of(doctorMock));

        Assertions.assertThrows(DuplicateResourceException.class, () -> doctorService.save(doctorMock));
        Mockito.verify(doctorRepository, Mockito.times(1)).findAll(ArgumentMatchers.any(Specification.class));
        Mockito.verify(doctorRepository, Mockito.never()).save(ArgumentMatchers.any(Doctor.class));
    }

    @Test
    void delete_deleteDoctor_WhenDoctorExists() {
        long id = 1L;
        Doctor doctorMock = DoctorUtil.createDoctor(id, "Henrique", "12345", "BA");

        Mockito.when(doctorRepository.findById(id))
                .thenReturn(Optional.of(doctorMock));
        Mockito.doNothing()
                .when(doctorRepository).delete(doctorMock);

        doctorService.delete(id);
        Mockito.verify(doctorRepository, Mockito.times(1)).findById(id);
        Mockito.verify(doctorRepository, Mockito.times(1)).delete(doctorMock);
    }

    @Test
    void delete_ThrowsResourceNotFoundException_WhenDoctorNotExists() {
        long id = 1L;

        Mockito.when(doctorRepository.findById(id))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> doctorService.delete(id));
        Mockito.verify(doctorRepository, Mockito.times(1)).findById(id);
        Mockito.verify(doctorRepository, Mockito.never()).delete(ArgumentMatchers.any(Doctor.class));
    }

    @Test
    void findSpecialties_ReturnListOfSpecialties_WhenDoctorExists() {
        long id = 1L;
        Specialty specialty = SpecialtyUtil.createSpecialty(1L, "Neurologista", "Descrição");
        Doctor doctorMock = DoctorUtil.createDoctor(id, "Henrique", "12345", "BA", specialty);
        specialty.getDoctors().add(doctorMock);

        Mockito.when(doctorRepository.findById(id))
                .thenReturn(Optional.of(doctorMock));

        List<Specialty> specialties = doctorService.findSpecialties(id);
        Specialty first = specialties.getFirst();

        Assertions.assertNotNull(specialties);
        Assertions.assertEquals(1, specialties.size());
        Assertions.assertEquals(1, first.getDoctors().size());
        Assertions.assertEquals(doctorMock, first.getDoctors().getFirst());
        Mockito.verify(doctorRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void findSpecialties_ThrowsResourceNotFoundException_WhenDoctorNotExists() {
        long id = 1L;

        Mockito.when(doctorRepository.findById(id))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> doctorService.findSpecialties(id));
        Mockito.verify(doctorRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void updateByParts_UpdateDoctor_WhenCorrectDataIsProvided() {
        Specialty specialtyCardio = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Specialty specialtyOrto = SpecialtyUtil.createSpecialty(2L, "Ortopedista", "Descrição ortopedista");
        Specialty specialtyNeuro = SpecialtyUtil.createSpecialty(3L, "Neurologista", "Descrição neurologista");

        long doctorId = 1L;
        Doctor doctorMock = DoctorUtil.createDoctor(doctorId, "Henrique", "12345", "BA", specialtyCardio, specialtyOrto);
        specialtyCardio.setDoctors(new ArrayList<>(List.of(doctorMock)));
        specialtyOrto.setDoctors(new ArrayList<>(List.of(doctorMock)));
        String newName = "Inara";

        ObjectNode jsonNodeRoot = mapper.createObjectNode();
        jsonNodeRoot.put("name", newName);

        ObjectNode jsonNodeSpe = mapper.createObjectNode();

        ArrayNode arrayNodeAdd = mapper.createArrayNode();
        arrayNodeAdd.add("Neurologista");

        ArrayNode arrayNodeRemove = mapper.createArrayNode();
        arrayNodeRemove.add("Ortopedista");

        jsonNodeSpe.set("add", arrayNodeAdd);
        jsonNodeSpe.set("remove", arrayNodeRemove);

        jsonNodeRoot.set("specialties", jsonNodeSpe);


        Mockito.when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctorMock));
        Mockito.when(specialtyService.findByOptionalFilters("Neurologista", null))
                .thenReturn(List.of(specialtyNeuro));
        Mockito.when(specialtyService.findByOptionalFilters("Ortopedista", null))
                .thenReturn(List.of(specialtyOrto));
        Mockito.when(doctorRepository.save(ArgumentMatchers.any(Doctor.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Doctor doctorUpdated = doctorService.updateByParts(doctorId, jsonNodeRoot);

        Assertions.assertNotNull(doctorUpdated);
        Assertions.assertEquals(doctorId, doctorUpdated.getId());
        Assertions.assertEquals(newName, doctorUpdated.getName());
        Assertions.assertEquals(doctorMock.getCrm(), doctorUpdated.getCrm());
        Assertions.assertEquals(doctorMock.getUf(), doctorUpdated.getUf());
        Assertions.assertTrue(doctorUpdated.getSpecialties().contains(specialtyNeuro));
        Assertions.assertTrue(doctorUpdated.getSpecialties().contains(specialtyCardio));
        Assertions.assertFalse(doctorUpdated.getSpecialties().contains(specialtyOrto));
        Assertions.assertEquals(2, doctorUpdated.getSpecialties().size());

        Mockito.verify(doctorRepository, Mockito.times(1)).findById(doctorId);
        Mockito.verify(doctorRepository, Mockito.times(1)).save(ArgumentMatchers.any(Doctor.class));
    }
}
