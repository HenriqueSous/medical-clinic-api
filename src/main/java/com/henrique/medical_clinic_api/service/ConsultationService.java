package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationRequestDTO;
import com.henrique.medical_clinic_api.exception.ConsultationNotFoundException;
import com.henrique.medical_clinic_api.mapper.ConsultationMapper;
import com.henrique.medical_clinic_api.model.Consultation;
import com.henrique.medical_clinic_api.model.ConsultationStatus;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.repository.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    public Consultation findById(long id) {
        return consultationRepository.findById(id).orElseThrow(() -> new ConsultationNotFoundException(id));
    }

    public List<Consultation> findByOptionalFilters(
            Long patientId,
            Long doctorId,
            LocalDate consultationDate,
            LocalTime consultationTime,
            String status,
            Integer duration
    ) {
        ConsultationStatus consultationStatus;
        if (status != null) {
            consultationStatus = ConsultationStatus.valueOf(status.toUpperCase());
        } else {
            consultationStatus = null;
        }

        return consultationRepository.findByOptionalFilters(
                patientId,
                doctorId,
                consultationDate,
                consultationTime,
                consultationStatus,
                duration
        );
    }

    @Transactional
    public Consultation save(ConsultationRequestDTO consultationRequestDTO) {
        Consultation consultation = consultationMapper.toEntity(consultationRequestDTO);

        Doctor doctor = doctorService.findById(consultationRequestDTO.doctorId());
        Patient patient = patientService.findById(consultationRequestDTO.patientId());

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
}
