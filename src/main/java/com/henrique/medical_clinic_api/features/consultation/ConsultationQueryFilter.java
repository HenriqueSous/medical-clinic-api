package com.henrique.medical_clinic_api.features.consultation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class ConsultationQueryFilter {
    private Long patientId;
    private Long doctorId;
    private LocalDate consultationDate;
    private LocalTime consultationTime;
    private String status;
    private Integer duration;

    public Specification<Consultation> toSpecification() {
        ConsultationStatus consultationStatus;
        if (!ObjectUtils.isEmpty(status)) {
            consultationStatus = ConsultationStatus.valueOf(status.toUpperCase());
        } else {
            consultationStatus = null;
        }

        return ConsultationSpecifications.equalsPatientId(patientId)
                .and(ConsultationSpecifications.equalsDoctorId(doctorId))
                .and(ConsultationSpecifications.equalsConsultationDate(consultationDate))
                .and(ConsultationSpecifications.equalsConsultationTime(consultationTime))
                .and(ConsultationSpecifications.equalsConsultationStatus(consultationStatus))
                .and(ConsultationSpecifications.equalsDuration(duration));
    }
}
