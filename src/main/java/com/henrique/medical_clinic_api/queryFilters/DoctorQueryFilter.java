package com.henrique.medical_clinic_api.queryFilters;

import com.henrique.medical_clinic_api.model.Doctor;
import com.henrique.medical_clinic_api.repository.specification.DoctorSpecification;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
public class DoctorQueryFilter {
    private String name;
    private String crm;
    private String uf;

    public Specification<Doctor> toSpecification() {
        return DoctorSpecification.startWithName(name)
                .and(DoctorSpecification.startWithCrm(crm))
                .and(DoctorSpecification.equalsUf(uf));
    }
}
