package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationRequestDTO;
import com.henrique.medical_clinic_api.exception.domain.DoctorScheduleConflictException;
import com.henrique.medical_clinic_api.exception.domain.DuplicateAppointmentDateException;
import com.henrique.medical_clinic_api.exception.resource.ResourceNotFoundException;
import com.henrique.medical_clinic_api.mapper.ConsultationMapper;
import com.henrique.medical_clinic_api.model.*;
import com.henrique.medical_clinic_api.repository.ConsultationRepository;
import com.henrique.medical_clinic_api.util.ConsultationUtil;
import com.henrique.medical_clinic_api.util.DoctorUtil;
import com.henrique.medical_clinic_api.util.PatientUtil;
import com.henrique.medical_clinic_api.util.SpecialtyUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

    @Captor
    private ArgumentCaptor<Consultation> consultationCaptor;

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

    @Test
    void save_SaveNewConsultation_WhenCorrectDataIsProvided() {
        Patient patientMock = PatientUtil.createPatient(1L, "Henrique", "12345678909");
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Pedro", "12345", "BA", true, specialtyMock);
        ConsultationUtil.setDoctorConsultations(doctorMock);

        ReflectionTestUtils.setField(consultationService, "consultationMapper", ConsultationMapper.INSTANCE);
        Mockito.when(doctorService.findById(1L))
                .thenReturn(doctorMock);
        Mockito.when(patientService.findById(1L))
                .thenReturn(patientMock);
        Mockito.when(consultationRepository.save(consultationCaptor.capture()))
                .thenAnswer(
                        inv -> {
                            Consultation c = inv.getArgument(0);
                            c.setId(10L);
                            return c;
                        }
                );

        ConsultationRequestDTO consultationRequestDTO = new ConsultationRequestDTO(1L, 1L,
                LocalDate.of(2026, 9, 24), LocalTime.of(16, 50), 30);
        Consultation saved = consultationService.save(consultationRequestDTO);

        Consultation consultationCaptured = consultationCaptor.getValue();
        Doctor capturedDoctor = consultationCaptured.getDoctor();

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(10L, consultationCaptured.getId());
        Assertions.assertEquals(patientMock.getCpf(), consultationCaptured.getPatient().getCpf());
        Assertions.assertEquals(doctorMock.getCrm()+doctorMock.getUf(), capturedDoctor.getCrm()+capturedDoctor.getUf());
        Assertions.assertEquals(consultationRequestDTO.consultationDate(), consultationCaptured.getConsultationDate());
        Assertions.assertEquals(consultationRequestDTO.consultationTime(), consultationCaptured.getConsultationTime());
        Assertions.assertEquals(consultationRequestDTO.duration(), consultationCaptured.getDuration());
        Assertions.assertEquals(ConsultationStatus.SCHEDULED, consultationCaptured.getStatus());

        Mockito.verify(consultationRepository, Mockito.times(1)).save(consultationCaptured);
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "16:10;30",
                    "16:30;30",
                    "15:50;30",
                    "16:00;50",
                    "16:00;40",
            },
            delimiter = ';'
    )
    void save_ThrowsDoctorScheduleConflictException_WhenNewConsultationOverlapsExistingOne(LocalTime localTime, int duration) {
        Patient patientMock = PatientUtil.createPatient(1L, "Henrique", "12345678909");
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Pedro", "12345", "BA", true, specialtyMock);
        ConsultationUtil.setDoctorConsultations(doctorMock);

        ReflectionTestUtils.setField(consultationService, "consultationMapper", ConsultationMapper.INSTANCE);
        Mockito.when(doctorService.findById(1L))
                .thenReturn(doctorMock);
        Mockito.when(patientService.findById(1L))
                .thenReturn(patientMock);

        Assertions.assertThrows(DoctorScheduleConflictException.class, () -> consultationService.save(
                new ConsultationRequestDTO(1L, 1L,
                        LocalDate.of(2026, 9, 24), localTime, duration)
        ));

        Mockito.verify(consultationRepository, Mockito.never()).save(ArgumentMatchers.any(Consultation.class));
    }

    @Test
    void save_SaveNewConsultation_WhenStartsExactlyWhenPreviousEnds() {
        Patient patientMock = PatientUtil.createPatient(1L, "Henrique", "12345678909");
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Pedro", "12345", "BA", true, specialtyMock);
        ConsultationUtil.setDoctorConsultations(doctorMock);

        ReflectionTestUtils.setField(consultationService, "consultationMapper", ConsultationMapper.INSTANCE);
        Mockito.when(doctorService.findById(1L))
                .thenReturn(doctorMock);
        Mockito.when(patientService.findById(1L))
                .thenReturn(patientMock);
        Mockito.when(consultationRepository.save(consultationCaptor.capture()))
                .thenAnswer(
                        inv -> {
                            Consultation c = inv.getArgument(0);
                            c.setId(10L);
                            return c;
                        }
                );

        ConsultationRequestDTO consultationRequestDTO = new ConsultationRequestDTO(1L, 1L,
                LocalDate.of(2026, 9, 24), LocalTime.of(16, 40), 30);
        Consultation saved = consultationService.save(consultationRequestDTO);

        Consultation consultationCaptured = consultationCaptor.getValue();
        Doctor capturedDoctor = consultationCaptured.getDoctor();

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(10L, consultationCaptured.getId());
        Assertions.assertEquals(patientMock.getCpf(), consultationCaptured.getPatient().getCpf());
        Assertions.assertEquals(doctorMock.getCrm()+doctorMock.getUf(), capturedDoctor.getCrm()+capturedDoctor.getUf());
        Assertions.assertEquals(consultationRequestDTO.consultationDate(), consultationCaptured.getConsultationDate());
        Assertions.assertEquals(consultationRequestDTO.consultationTime(), consultationCaptured.getConsultationTime());
        Assertions.assertEquals(consultationRequestDTO.duration(), consultationCaptured.getDuration());
        Assertions.assertEquals(ConsultationStatus.SCHEDULED, consultationCaptured.getStatus());

        Mockito.verify(consultationRepository, Mockito.times(1)).save(consultationCaptured);
    }

    @Test
    void save_UsesDefaultDuration_WhenDurationIsNull() {
        Patient patientMock = PatientUtil.createPatient(1L, "Henrique", "12345678909");
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Pedro", "12345", "BA", true, specialtyMock);
        ConsultationUtil.setDoctorConsultations(doctorMock);

        ReflectionTestUtils.setField(consultationService, "consultationMapper", ConsultationMapper.INSTANCE);
        Mockito.when(doctorService.findById(1L))
                .thenReturn(doctorMock);
        Mockito.when(patientService.findById(1L))
                .thenReturn(patientMock);
        Mockito.when(consultationRepository.save(consultationCaptor.capture()))
                .thenAnswer(
                        inv -> {
                            Consultation c = inv.getArgument(0);
                            c.setId(10L);
                            return c;
                        }
                );

        ConsultationRequestDTO consultationRequestDTO = new ConsultationRequestDTO(1L, 1L,
                LocalDate.of(2026, 9, 24), LocalTime.of(16, 50), null);
        Consultation saved = consultationService.save(consultationRequestDTO);

        Consultation consultationCaptured = consultationCaptor.getValue();
        Doctor capturedDoctor = consultationCaptured.getDoctor();

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(10L, consultationCaptured.getId());
        Assertions.assertEquals(patientMock.getCpf(), consultationCaptured.getPatient().getCpf());
        Assertions.assertEquals(doctorMock.getCrm()+doctorMock.getUf(), capturedDoctor.getCrm()+capturedDoctor.getUf());
        Assertions.assertEquals(consultationRequestDTO.consultationDate(), consultationCaptured.getConsultationDate());
        Assertions.assertEquals(consultationRequestDTO.consultationTime(), consultationCaptured.getConsultationTime());
        Assertions.assertEquals(30, consultationCaptured.getDuration());
        Assertions.assertEquals(ConsultationStatus.SCHEDULED, consultationCaptured.getStatus());

        Mockito.verify(consultationRepository, Mockito.times(1)).save(consultationCaptured);
    }

    @Test
    void save_ThrowsDuplicateAppointmentDateException_WhenPatientAlreadyHasAppointmentForTheDay() {
        Patient patientMock = PatientUtil.createPatient(1L, "Henrique", "12345678909");
        Specialty specialtyMock = SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista");
        Doctor doctorMock = DoctorUtil.createDoctor(1L, "Pedro", "12345", "BA", true, specialtyMock);
        ConsultationUtil.setDoctorConsultations(doctorMock);

        ReflectionTestUtils.setField(consultationService, "consultationMapper", ConsultationMapper.INSTANCE);
        Mockito.when(doctorService.findById(1L))
                .thenReturn(doctorMock);
        Mockito.when(patientService.findById(1L))
                .thenReturn(patientMock);
        Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                .thenReturn(List.of(ConsultationUtil.createConsultation(
                        5L,
                        patientMock,
                        doctorMock,
                        LocalDateTime.of(2026, 10, 13, 9, 20)
                )));

        ConsultationRequestDTO consultationRequestDTO = new ConsultationRequestDTO(1L, 1L,
                LocalDate.of(2026, 9, 24), LocalTime.of(16, 10), 30);

        Assertions.assertThrows(DuplicateAppointmentDateException.class, () -> consultationService.save(consultationRequestDTO));

        Mockito.verify(consultationRepository, Mockito.never()).save(ArgumentMatchers.any(Consultation.class));
    }
}
