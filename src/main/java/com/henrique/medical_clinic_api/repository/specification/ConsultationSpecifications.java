package com.henrique.medical_clinic_api.repository.specification;

import com.henrique.medical_clinic_api.model.Consultation;
import com.henrique.medical_clinic_api.model.ConsultationStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;

public class ConsultationSpecifications {
    public static Specification<Consultation> equalsPatientId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(id)) return null;
            return criteriaBuilder.equal(root.get("patient").get("id"), id);
        };
    }

    public static Specification<Consultation> equalsDoctorId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(id)) return null;
            return criteriaBuilder.equal(root.get("doctor").get("id"), id);
        };
    }

    public static Specification<Consultation> equalsConsultationDate(LocalDate consultationDate) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(consultationDate)) return null;
            return criteriaBuilder.equal(root.get("consultationDate"), consultationDate);
        };
    }

    public static Specification<Consultation> equalsConsultationTime(LocalTime consultationTime) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(consultationTime)) return null;
            return criteriaBuilder.equal(root.get("consultationTime"), consultationTime);
        };
    }

    public static Specification<Consultation> equalsConsultationStatus(ConsultationStatus consultationStatus) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(consultationStatus)) return null;
            return criteriaBuilder.equal(root.get("status"), consultationStatus);
        };
    }

    public static Specification<Consultation> equalsDuration(Integer duration) {
        return (root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(duration)) return null;
            return criteriaBuilder.equal(root.get("duration"), duration);
        };
    }
}
