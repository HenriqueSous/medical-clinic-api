package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.doctor.Doctor;
import com.henrique.medical_clinic_api.features.doctor.DoctorService;
import com.henrique.medical_clinic_api.features.doctor.DoctorUtil;
import com.henrique.medical_clinic_api.features.patient.Patient;
import com.henrique.medical_clinic_api.features.patient.PatientService;
import com.henrique.medical_clinic_api.features.patient.PatientUtil;
import com.henrique.medical_clinic_api.features.specialty.SpecialtyUtil;
import com.henrique.medical_clinic_api.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

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

    @Spy
    private ConsultationMapper consultationMapper = ConsultationMapper.INSTANCE;

    @Nested
    class FindById {
        @Test
        void findById_ReturnConsultation_WhenConsultationExists() {
            long id = 1L;
            Consultation consultationMock = ConsultationUtil.createConsultation(id);

            Mockito.when(consultationRepository.findById(consultationMock.getId()))
                    .thenReturn(Optional.of(consultationMock));

            Consultation consultationFound = consultationService.findById(1L);

            Assertions.assertNotNull(consultationFound);
            Assertions.assertEquals(id, consultationFound.getId());
        }

        @Test
        void findById_ThrowsResourceNotFoundException_WhenConsultationNotExists() {
            Consultation consultationMock = ConsultationUtil.createConsultation(1L);

            Mockito.when(consultationRepository.findById(consultationMock.getId()))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> consultationService.findById(1L));
        }
    }

    @Nested
    class Save {
        private Patient patientMock;
        private Doctor doctorMock;

        @BeforeEach
        void setup() {
            patientMock = PatientUtil.createPatient(1L, "Henrique", "12345678909");
            doctorMock = DoctorUtil.createDoctor(
                    1L, "Pedro", "12345", "BA", true,
                    SpecialtyUtil.createSpecialty(1L, "Cardiologista", "Descrição cardiologista")
            );
        }

        @Test
        void save_SaveNewConsultation_WhenCorrectDataIsProvided() {
            Mockito.when(doctorService.findById(1L))
                    .thenReturn(doctorMock);
            Mockito.when(patientService.findById(1L))
                    .thenReturn(patientMock);
            Mockito.when(consultationRepository.save(ArgumentMatchers.any(Consultation.class)))
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

            Assertions.assertNotNull(saved);
            Assertions.assertEquals(10L, saved.getId());
            Assertions.assertEquals(patientMock.getCpf(), saved.getPatient().getCpf());
            Assertions.assertSame(doctorMock, saved.getDoctor());
            Assertions.assertEquals(consultationRequestDTO.consultationDate(), saved.getConsultationDate());
            Assertions.assertEquals(consultationRequestDTO.consultationTime(), saved.getConsultationTime());
            Assertions.assertEquals(consultationRequestDTO.duration(), saved.getDuration());
            Assertions.assertEquals(ConsultationStatus.SCHEDULED, saved.getStatus());

            Mockito.verify(consultationRepository, Mockito.times(1)).save(ArgumentMatchers.any(Consultation.class));
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
            Patient patient2 = PatientUtil.createPatient(2L, "Inara", "09876543212");

            Mockito.when(doctorService.findById(1L))
                    .thenReturn(doctorMock);
            Mockito.when(patientService.findById(1L))
                    .thenReturn(patientMock);
            Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                    .thenReturn(List.of())
                    .thenReturn(List.of(ConsultationUtil.createConsultation(1L, patient2, doctorMock, LocalDateTime.of(2026, 9, 24, 16, 10), 30)));

            Assertions.assertThrows(DoctorScheduleConflictException.class, () -> consultationService.save(
                    new ConsultationRequestDTO(1L, 1L,
                            LocalDate.of(2026, 9, 24), localTime, duration)
            ));

            Mockito.verify(consultationRepository, Mockito.never()).save(ArgumentMatchers.any(Consultation.class));
        }

        @Test
        void save_SaveNewConsultation_WhenStartsExactlyWhenPreviousEnds() {
            Mockito.when(doctorService.findById(1L))
                    .thenReturn(doctorMock);
            Mockito.when(patientService.findById(1L))
                    .thenReturn(patientMock);
            Mockito.when(consultationRepository.save(ArgumentMatchers.any(Consultation.class)))
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

            Assertions.assertNotNull(saved);
            Assertions.assertEquals(10L, saved.getId());
            Assertions.assertEquals(patientMock.getCpf(), saved.getPatient().getCpf());
            Assertions.assertSame(doctorMock, saved.getDoctor());
            Assertions.assertEquals(consultationRequestDTO.consultationDate(), saved.getConsultationDate());
            Assertions.assertEquals(consultationRequestDTO.consultationTime(), saved.getConsultationTime());
            Assertions.assertEquals(consultationRequestDTO.duration(), saved.getDuration());
            Assertions.assertEquals(ConsultationStatus.SCHEDULED, saved.getStatus());

            Mockito.verify(consultationRepository, Mockito.times(1)).save(ArgumentMatchers.any(Consultation.class));
        }

        @Test
        void save_UsesDefaultDuration_WhenDurationIsNull() {
            Mockito.when(doctorService.findById(1L))
                    .thenReturn(doctorMock);
            Mockito.when(patientService.findById(1L))
                    .thenReturn(patientMock);
            Mockito.when(consultationRepository.save(ArgumentMatchers.any(Consultation.class)))
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

            Assertions.assertNotNull(saved);
            Assertions.assertEquals(10L, saved.getId());
            Assertions.assertEquals(patientMock.getCpf(), saved.getPatient().getCpf());
            Assertions.assertSame(doctorMock, saved.getDoctor());
            Assertions.assertEquals(consultationRequestDTO.consultationDate(), saved.getConsultationDate());
            Assertions.assertEquals(consultationRequestDTO.consultationTime(), saved.getConsultationTime());
            Assertions.assertEquals(30, saved.getDuration());
            Assertions.assertEquals(ConsultationStatus.SCHEDULED, saved.getStatus());

            Mockito.verify(consultationRepository, Mockito.times(1)).save(ArgumentMatchers.any(Consultation.class));
        }

        @Test
        void save_ThrowsDuplicateAppointmentDateException_WhenPatientAlreadyHasAppointmentForTheDay() {
            Mockito.when(doctorService.findById(1L))
                    .thenReturn(doctorMock);
            Mockito.when(patientService.findById(1L))
                    .thenReturn(patientMock);
            Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                    .thenReturn(List.of(ConsultationUtil.createConsultation(
                            5L,
                            patientMock,
                            doctorMock,
                            LocalDateTime.of(2026, 10, 13, 9, 20),
                            30
                    )));

            ConsultationRequestDTO consultationRequestDTO = new ConsultationRequestDTO(1L, 1L,
                    LocalDate.of(2026, 9, 24), LocalTime.of(16, 10), 30);

            Assertions.assertThrows(DuplicateAppointmentDateException.class, () -> consultationService.save(consultationRequestDTO));

            Mockito.verify(consultationRepository, Mockito.never()).save(ArgumentMatchers.any(Consultation.class));
        }
    }

    @Nested
    class UpdateByParts {
        private long consultationId;
        private Consultation consultationMock;
        private Patient patientMock;
        private Doctor doctorMock;
        private final ObjectMapper mapper = new ObjectMapper();

        @BeforeEach
        void setup() {
            consultationId = 1L;
            consultationMock = ConsultationUtil.createConsultation(consultationId);
            patientMock = consultationMock.getPatient();
            doctorMock = consultationMock.getDoctor();
        }

        @Test
        void updateByParts_UpdateAllConsultationData_WhenCorrectDataIsProvided() {
            LocalDate newLocalDate = LocalDate.of(2026, 11, 5);
            LocalTime newLocalTime = LocalTime.of(9, 30);
            int newDuration = 20;
            ConsultationStatus newStatus = ConsultationStatus.CONFIRMED;

            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("consultationDate", newLocalDate.toString());
            jsonNode.put("consultationTime", newLocalTime.toString());
            jsonNode.put("duration", newDuration);
            jsonNode.put("status", newStatus.toString().toLowerCase());

            Mockito.when(consultationRepository.findById(consultationId))
                    .thenReturn(Optional.of(consultationMock));
            Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                    .thenReturn(List.of());
            Mockito.when(doctorService.findById(consultationMock.getDoctor().getId()))
                    .thenReturn(consultationMock.getDoctor());
            Mockito.when(consultationRepository.save(ArgumentMatchers.any(Consultation.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Consultation consultationUpdated = consultationService.updateByParts(consultationId, jsonNode);

            Patient patientOfConsultationUpdated = consultationUpdated.getPatient();
            Doctor doctorOfConsultationUpdated = consultationUpdated.getDoctor();

            Assertions.assertNotNull(consultationUpdated);
            Assertions.assertEquals(consultationId, consultationUpdated.getId());
            Assertions.assertEquals(newLocalDate, consultationUpdated.getConsultationDate());
            Assertions.assertEquals(newLocalTime, consultationUpdated.getConsultationTime());
            Assertions.assertEquals(newDuration, consultationUpdated.getDuration());
            Assertions.assertEquals(newStatus, consultationUpdated.getStatus());

            Assertions.assertEquals(patientMock.getId(), patientOfConsultationUpdated.getId());
            Assertions.assertEquals(patientMock.getName(), patientOfConsultationUpdated.getName());
            Assertions.assertEquals(patientMock.getCpf(), patientOfConsultationUpdated.getCpf());

            Assertions.assertEquals(doctorMock.getId(), doctorOfConsultationUpdated.getId());
            Assertions.assertEquals(doctorMock.getName(), doctorOfConsultationUpdated.getName());
            Assertions.assertEquals(doctorMock.getCrm(), doctorOfConsultationUpdated.getCrm());
            Assertions.assertEquals(doctorMock.getUf(), doctorOfConsultationUpdated.getUf());
            Assertions.assertEquals(doctorMock.getSpecialties(), doctorOfConsultationUpdated.getSpecialties());

            Mockito.verify(consultationRepository, Mockito.times(1)).save(ArgumentMatchers.any(Consultation.class));
        }

        @Test
        void updateByParts_UpdateOnlyConsultationDate_WhenOnlyConsultationDateIsProvided() {
            LocalDate newLocalDate = LocalDate.of(2026, 11, 5);

            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("consultationDate", newLocalDate.toString());

            Mockito.when(consultationRepository.findById(consultationId))
                    .thenReturn(Optional.of(consultationMock));
            Mockito.when(doctorService.findById(consultationMock.getDoctor().getId()))
                    .thenReturn(consultationMock.getDoctor());
            Mockito.when(consultationRepository.save(ArgumentMatchers.any(Consultation.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Consultation consultationUpdated = consultationService.updateByParts(consultationId, jsonNode);

            Patient patientOfConsultationUpdated = consultationUpdated.getPatient();
            Doctor doctorOfConsultationUpdated = consultationUpdated.getDoctor();

            Assertions.assertNotNull(consultationUpdated);
            Assertions.assertEquals(consultationId, consultationUpdated.getId());
            Assertions.assertEquals(newLocalDate, consultationUpdated.getConsultationDate());
            Assertions.assertEquals(consultationMock.getConsultationTime(), consultationUpdated.getConsultationTime());
            Assertions.assertEquals(consultationMock.getDuration(), consultationUpdated.getDuration());
            Assertions.assertEquals(consultationMock.getStatus(), consultationUpdated.getStatus());

            Assertions.assertEquals(patientMock.getId(), patientOfConsultationUpdated.getId());
            Assertions.assertEquals(patientMock.getName(), patientOfConsultationUpdated.getName());
            Assertions.assertEquals(patientMock.getCpf(), patientOfConsultationUpdated.getCpf());

            Assertions.assertEquals(doctorMock.getId(), doctorOfConsultationUpdated.getId());
            Assertions.assertEquals(doctorMock.getName(), doctorOfConsultationUpdated.getName());
            Assertions.assertEquals(doctorMock.getCrm(), doctorOfConsultationUpdated.getCrm());
            Assertions.assertEquals(doctorMock.getUf(), doctorOfConsultationUpdated.getUf());
            Assertions.assertEquals(doctorMock.getSpecialties(), doctorOfConsultationUpdated.getSpecialties());

            Mockito.verify(consultationRepository, Mockito.times(1)).save(ArgumentMatchers.any(Consultation.class));
        }

        @Test
        void updateByParts_UpdateOnlyConsultationTime_WhenOnlyConsultationTimeIsProvided() {
            LocalTime newLocalTime = LocalTime.of(9, 30);

            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("consultationTime", newLocalTime.toString());

            Mockito.when(consultationRepository.findById(consultationId))
                    .thenReturn(Optional.of(consultationMock));
            Mockito.when(doctorService.findById(consultationMock.getDoctor().getId()))
                    .thenReturn(consultationMock.getDoctor());
            Mockito.when(consultationRepository.save(ArgumentMatchers.any(Consultation.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Consultation consultationUpdated = consultationService.updateByParts(consultationId, jsonNode);

            Patient patientOfConsultationUpdated = consultationUpdated.getPatient();
            Doctor doctorOfConsultationUpdated = consultationUpdated.getDoctor();

            Assertions.assertNotNull(consultationUpdated);
            Assertions.assertEquals(consultationId, consultationUpdated.getId());
            Assertions.assertEquals(consultationMock.getConsultationDate(), consultationUpdated.getConsultationDate());
            Assertions.assertEquals(newLocalTime, consultationUpdated.getConsultationTime());
            Assertions.assertEquals(consultationMock.getDuration(), consultationUpdated.getDuration());
            Assertions.assertEquals(consultationMock.getStatus(), consultationUpdated.getStatus());

            Assertions.assertEquals(patientMock.getId(), patientOfConsultationUpdated.getId());
            Assertions.assertEquals(patientMock.getName(), patientOfConsultationUpdated.getName());
            Assertions.assertEquals(patientMock.getCpf(), patientOfConsultationUpdated.getCpf());

            Assertions.assertEquals(doctorMock.getId(), doctorOfConsultationUpdated.getId());
            Assertions.assertEquals(doctorMock.getName(), doctorOfConsultationUpdated.getName());
            Assertions.assertEquals(doctorMock.getCrm(), doctorOfConsultationUpdated.getCrm());
            Assertions.assertEquals(doctorMock.getUf(), doctorOfConsultationUpdated.getUf());
            Assertions.assertEquals(doctorMock.getSpecialties(), doctorOfConsultationUpdated.getSpecialties());

            Mockito.verify(consultationRepository, Mockito.times(1)).save(ArgumentMatchers.any(Consultation.class));
        }

        @Test
        void updateByParts_UpdateOnlyDuration_WhenOnlyDurationIsProvided() {
            int newDuration = 20;

            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("duration", newDuration);

            Mockito.when(consultationRepository.findById(consultationId))
                    .thenReturn(Optional.of(consultationMock));
            Mockito.when(doctorService.findById(consultationMock.getDoctor().getId()))
                    .thenReturn(consultationMock.getDoctor());
            Mockito.when(consultationRepository.save(ArgumentMatchers.any(Consultation.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Consultation consultationUpdated = consultationService.updateByParts(consultationId, jsonNode);

            Patient patientOfConsultationUpdated = consultationUpdated.getPatient();
            Doctor doctorOfConsultationUpdated = consultationUpdated.getDoctor();

            Assertions.assertNotNull(consultationUpdated);
            Assertions.assertEquals(consultationId, consultationUpdated.getId());
            Assertions.assertEquals(consultationMock.getConsultationDate(), consultationUpdated.getConsultationDate());
            Assertions.assertEquals(consultationMock.getConsultationTime(), consultationUpdated.getConsultationTime());
            Assertions.assertEquals(newDuration, consultationUpdated.getDuration());
            Assertions.assertEquals(consultationMock.getStatus(), consultationUpdated.getStatus());

            Assertions.assertEquals(patientMock.getId(), patientOfConsultationUpdated.getId());
            Assertions.assertEquals(patientMock.getName(), patientOfConsultationUpdated.getName());
            Assertions.assertEquals(patientMock.getCpf(), patientOfConsultationUpdated.getCpf());

            Assertions.assertEquals(doctorMock.getId(), doctorOfConsultationUpdated.getId());
            Assertions.assertEquals(doctorMock.getName(), doctorOfConsultationUpdated.getName());
            Assertions.assertEquals(doctorMock.getCrm(), doctorOfConsultationUpdated.getCrm());
            Assertions.assertEquals(doctorMock.getUf(), doctorOfConsultationUpdated.getUf());
            Assertions.assertEquals(doctorMock.getSpecialties(), doctorOfConsultationUpdated.getSpecialties());

            Mockito.verify(consultationRepository, Mockito.times(1)).save(ArgumentMatchers.any(Consultation.class));
        }

        @Test
        void updateByParts_UpdateStatusDuration_WhenOnlyStatusIsProvided() {
            ConsultationStatus newStatus = ConsultationStatus.CONFIRMED;
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("status", newStatus.toString().toLowerCase());

            Mockito.when(consultationRepository.findById(consultationId))
                    .thenReturn(Optional.of(consultationMock));
            Mockito.when(doctorService.findById(consultationMock.getDoctor().getId()))
                    .thenReturn(consultationMock.getDoctor());
            Mockito.when(consultationRepository.save(ArgumentMatchers.any(Consultation.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Consultation consultationUpdated = consultationService.updateByParts(consultationId, jsonNode);

            Patient patientOfConsultationUpdated = consultationUpdated.getPatient();
            Doctor doctorOfConsultationUpdated = consultationUpdated.getDoctor();

            Assertions.assertNotNull(consultationUpdated);
            Assertions.assertEquals(consultationId, consultationUpdated.getId());
            Assertions.assertEquals(consultationMock.getConsultationDate(), consultationUpdated.getConsultationDate());
            Assertions.assertEquals(consultationMock.getConsultationTime(), consultationUpdated.getConsultationTime());
            Assertions.assertEquals(consultationMock.getDuration(), consultationUpdated.getDuration());
            Assertions.assertEquals(newStatus, consultationUpdated.getStatus());

            Assertions.assertEquals(patientMock.getId(), patientOfConsultationUpdated.getId());
            Assertions.assertEquals(patientMock.getName(), patientOfConsultationUpdated.getName());
            Assertions.assertEquals(patientMock.getCpf(), patientOfConsultationUpdated.getCpf());

            Assertions.assertEquals(doctorMock.getId(), doctorOfConsultationUpdated.getId());
            Assertions.assertEquals(doctorMock.getName(), doctorOfConsultationUpdated.getName());
            Assertions.assertEquals(doctorMock.getCrm(), doctorOfConsultationUpdated.getCrm());
            Assertions.assertEquals(doctorMock.getUf(), doctorOfConsultationUpdated.getUf());
            Assertions.assertEquals(doctorMock.getSpecialties(), doctorOfConsultationUpdated.getSpecialties());

            Mockito.verify(consultationRepository, Mockito.times(1)).save(ArgumentMatchers.any(Consultation.class));
        }

        @ParameterizedTest
        @CsvSource(
                value = {
                        "2026-11-05T09:30;2026-11-05;;false;30;2",
                        "2026-11-05T09:30;;09:40;true;30;2",
                        "2026-11-05T09:30;2026-11-05;09:40;false;30;2",
                        "2026-11-05T09:30;2026-11-05;09:20;false;30;2",
                        "2026-11-05T09:30;2026-11-05;09:20;false;30;2",
                        "2026-11-05T09:30;2026-11-05;09:20;false;60;2",
                },
                delimiter = ';'
        )
        void updateByParts_ThrowsDoctorScheduleConflictException_WhenThereIsConflictInTheDoctorSchedule(
                LocalDateTime dateTimeFirstConsultation,
                LocalDate dateUpdated,
                LocalTime timeUpdated,
                boolean sameDate,
                int duration,
                long id
        ) {
            Consultation firstConsultationMock = ConsultationUtil.createConsultation(1L);
            Doctor doctor = firstConsultationMock.getDoctor();

            firstConsultationMock.setConsultationDate(dateTimeFirstConsultation.toLocalDate());
            firstConsultationMock.setConsultationTime(dateTimeFirstConsultation.toLocalTime());

            Patient patientForUpdated = PatientUtil.createPatient(2L, "Marcelo", "09876545678");
            Patient patient2 = PatientUtil.createPatient(2L, "Inara", "09876543212");

            Consultation consultationUpdate = ConsultationUtil.createConsultation(
                    id,
                    patientForUpdated,
                    doctor,
                    dateTimeFirstConsultation,
                    duration
            );
            if (sameDate) {
                consultationUpdate.setConsultationTime(dateTimeFirstConsultation.toLocalTime().plusHours(3));
            } else {
                consultationUpdate.setConsultationDate(dateTimeFirstConsultation.toLocalDate().plusDays(3));
            }

            ObjectNode jsonNode = mapper.createObjectNode();
            if (dateUpdated != null) {
                jsonNode.put("consultationDate", dateUpdated.toString());
            }
            if (timeUpdated != null) {
                jsonNode.put("consultationTime", timeUpdated.toString());
            }

            Mockito.when(consultationRepository.findById(id))
                    .thenReturn(Optional.of(consultationUpdate));
            Mockito.when(doctorService.findById(firstConsultationMock.getDoctor().getId()))
                    .thenReturn(firstConsultationMock.getDoctor());
            Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                    .thenReturn(List.of())
                    .thenReturn(List.of(ConsultationUtil.createConsultation(1L, patient2, doctor, dateTimeFirstConsultation, 30)));

            Assertions.assertThrows(DoctorScheduleConflictException.class, () -> consultationService.updateByParts(id, jsonNode));

            Mockito.verify(consultationRepository, Mockito.never()).save(ArgumentMatchers.any(Consultation.class));
        }

        @Test
        void updateByParts_ThrowsDuplicateAppointmentDateException_WhenExistingPatientSchedule() {
            Consultation consultationToUpdate = ConsultationUtil.createConsultation(
                    2L,
                    patientMock,
                    DoctorUtil.createDoctor(),
                    LocalDateTime.of(2026, 12, 5, 12, 20),
                    30
            );

            LocalDate newDate = consultationMock.getConsultationDate();

            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("consultationDate", newDate.toString());

            Mockito.when(consultationRepository.findById(2L))
                    .thenReturn(Optional.of(consultationToUpdate));
            Mockito.when(consultationRepository.findAll(ArgumentMatchers.any(Specification.class)))
                    .thenReturn(List.of(consultationMock));

            Assertions.assertThrows(
                    DuplicateAppointmentDateException.class, () -> consultationService.updateByParts(2L, jsonNode)
            );
            Mockito.verify(consultationRepository, Mockito.never()).save(ArgumentMatchers.any(Consultation.class));
        }
    }

    @Nested
    class Delete {
        @Test
        void delete_DeleteConsultation_WhenIdIsFound() {
            Consultation consultationMock = ConsultationUtil.createConsultation(1L);

            Mockito.when(consultationRepository.findById(1L))
                    .thenReturn(Optional.of(consultationMock));

            consultationService.delete(1L);
            Mockito.verify(consultationRepository, Mockito.times(1)).delete(consultationMock);
        }

        @Test
        void delete_ThrowsResourceNotFoundException_WhenIdNotFound() {
            Mockito.when(consultationRepository.findById(1L))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(ResourceNotFoundException.class, () -> consultationService.delete(1L));
            Mockito.verify(consultationRepository, Mockito.never()).delete(ArgumentMatchers.any(Consultation.class));
        }
    }
}
