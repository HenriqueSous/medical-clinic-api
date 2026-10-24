package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.doctor.DoctorService;
import com.henrique.medical_clinic_api.features.patient.PatientService;
import com.henrique.medical_clinic_api.shared.exception.ResourceNotFoundException;
import com.henrique.medical_clinic_api.features.doctor.Doctor;
import com.henrique.medical_clinic_api.features.patient.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsultationService {
    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    private static final int STANDARD_CONSULTATION_DURATION = 30;

    private static final int MINIMUM_CONSULTATION_DURATION = 15;

    public Consultation findById(long id) {
        return consultationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Consultation", id));
    }

    public List<Consultation> find(ConsultationQueryFilter filter) {
        return consultationRepository.findAll(filter.toSpecification());
    }

    @Transactional
    public Consultation save(ConsultationRequestDTO consultationRequestDTO) {
        long patientId = consultationRequestDTO.patientId();
        long doctorId = consultationRequestDTO.doctorId();
        Doctor doctor = doctorService.findById(doctorId);
        Patient patient = patientService.findById(patientId);

        int duration = checkDuration(consultationRequestDTO.duration());

        validateAppointment(
                patientId,
                doctorId,
                consultationRequestDTO.consultationDate(),
                consultationRequestDTO.consultationTime(),
                duration,
                0
        );

        Consultation consultation = consultationMapper.toEntity(consultationRequestDTO, duration);

        consultation.setDoctor(doctor);
        consultation.setPatient(patient);

        doctor.getConsultations().add(consultation);
        patient.getConsultations().add(consultation);

        return consultationRepository.save(consultation);
    }

    public void delete(long id) {
        Consultation consultation = findById(id);
        consultationRepository.delete(consultation);
    }

    @Transactional
    public Consultation updateByParts(long id, JsonNode jsonNode) {
        Consultation consultation = findById(id);

        LocalDate newLocalDate = null;
        LocalTime newLocalTime = null;
        int newDuration = consultation.getDuration();

        if (jsonNode.has("consultationDate")) {
            newLocalDate = LocalDate.parse(jsonNode.get("consultationDate").asString());
        }
        if (jsonNode.has("consultationTime")) {
            newLocalTime = LocalTime.parse(jsonNode.get("consultationTime").asString());
        }
        if (jsonNode.has("duration")) {
            newDuration = checkDuration(jsonNode.get("duration").asInt());
        }

        if (newLocalDate == null && newLocalTime == null) {
            newLocalDate = consultation.getConsultationDate();
            newLocalTime = consultation.getConsultationTime();
        } else if (newLocalDate != null && newLocalTime == null) {
            newLocalTime = consultation.getConsultationTime();
        } else if (newLocalDate == null) {
            newLocalDate = consultation.getConsultationDate();
        }

        validateAppointment(
                consultation.getPatient().getId(),
                consultation.getDoctor().getId(),
                newLocalDate,
                newLocalTime,
                newDuration,
                id
        );

        if (newLocalDate != null) {
            consultation.setConsultationDate(newLocalDate);
        }
        if (newLocalTime != null) {
            consultation.setConsultationTime(newLocalTime);
        }
        if (newDuration != consultation.getDuration()) {
            consultation.setDuration(newDuration);
        }
        if (jsonNode.has("status")) {
            consultation.setStatus(
                    ConsultationStatus.valueOf(jsonNode.get("status").asString().toUpperCase())
            );
        }

        return consultationRepository.save(consultation);
    }

    private void validateAppointment(long patientId, long doctorId, LocalDate date, LocalTime time, int duration, long exceptThisId) {
        checkExistingPatientSchedule(patientId, date, exceptThisId);

        checkDoctorSchedule(
                doctorId,
                LocalDateTime.of(date, time),
                duration,
                exceptThisId
        );
    }

    private void checkDoctorSchedule(long doctorId, LocalDateTime dateTime, long duration, long exceptThisId) {
        Doctor doctor = doctorService.findById(doctorId);
        LocalDateTime dateTimePlus = dateTime.plusMinutes(duration);

        for (Consultation consultation : doctor.getConsultations()) {
            LocalDateTime scheduledDateTime = LocalDateTime.of(consultation.getConsultationDate(), consultation.getConsultationTime());

            if (exceptThisId != consultation.getId() && consultation.getStatus() != ConsultationStatus.CANCELED) {
                LocalDateTime scheduledDateTimePlus = scheduledDateTime.plusMinutes(consultation.getDuration());
                if (
                        dateTime.isAfter(scheduledDateTime) && dateTime.isBefore(scheduledDateTimePlus) ||
                        dateTimePlus.isAfter(scheduledDateTime) && dateTimePlus.isBefore(scheduledDateTimePlus) ||
                        dateTime.isBefore(scheduledDateTime) && dateTimePlus.isAfter(scheduledDateTimePlus) ||
                        dateTime.equals(scheduledDateTime) ||
                        dateTimePlus.equals(scheduledDateTimePlus)
                ) {
                    throw new DoctorScheduleConflictException(doctor.getId(), dateTime);
                }
            }
        }
    }

    private void checkExistingPatientSchedule(long patientId, LocalDate date, long exceptThisId) {
        ConsultationQueryFilter filterPatientConsultationDate = ConsultationQueryFilter.builder()
                .patientId(patientId)
                .consultationDate(date)
                .build();

        ArrayList<Consultation> consultations = new ArrayList<>(find(filterPatientConsultationDate));
        consultations.removeIf(con -> con.getId() == exceptThisId);

        if (!consultations.isEmpty()) {
            throw new DuplicateAppointmentDateException(patientId, date);
        }
    }

    private int checkDuration(Integer duration) {
        if (duration == null) {
            return STANDARD_CONSULTATION_DURATION;
        } else if (duration < 15) {
            throw new AppointmentDurationBelowMinimumException(duration, MINIMUM_CONSULTATION_DURATION);
        }
        return duration;
    }

    public void cancelOverdueAppointments() {
        LocalDateTime dateTimeMinusOneDay = LocalDateTime.now().minusHours(24);

        for (Consultation consultation : find(ConsultationQueryFilter.builder().build())) {
            LocalDateTime dateTime = LocalDateTime.of(consultation.getConsultationDate(), consultation.getConsultationTime());
            if (dateTime.isBefore(dateTimeMinusOneDay)) {
                if (consultation.getStatus().equals(ConsultationStatus.SCHEDULED) || consultation.getStatus().equals(ConsultationStatus.CONFIRMED)) {
                    consultation.setStatus(ConsultationStatus.CANCELED);
                    consultationRepository.save(consultation);
                }
            }
        }
    }
}
