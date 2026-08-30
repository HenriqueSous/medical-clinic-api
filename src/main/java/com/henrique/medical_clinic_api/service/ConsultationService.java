package com.henrique.medical_clinic_api.service;

import com.henrique.medical_clinic_api.dto.consultation.ConsultationRequestDTO;
import com.henrique.medical_clinic_api.exception.ConsultationNotFoundException;
import com.henrique.medical_clinic_api.mapper.ConsultationMapper;
import com.henrique.medical_clinic_api.model.Consultation;
import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.model.Patient;
import com.henrique.medical_clinic_api.queryFilters.ConsultationQueryFilter;
import com.henrique.medical_clinic_api.repository.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Consultation findById(long id) {
        return consultationRepository.findById(id).orElseThrow(() -> new ConsultationNotFoundException(id));
    }

    public List<Consultation> find(ConsultationQueryFilter filter) {
        return consultationRepository.findAll(filter.toSpecification());
    }

    @Transactional
    public Consultation save(ConsultationRequestDTO consultationRequestDTO) {
        Integer duration = consultationRequestDTO.duration();

        if (consultationRequestDTO.duration() == null) {
            duration = STANDARD_CONSULTATION_DURATION;
        }

        Consultation consultation = consultationMapper.toEntity(consultationRequestDTO, duration);

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
