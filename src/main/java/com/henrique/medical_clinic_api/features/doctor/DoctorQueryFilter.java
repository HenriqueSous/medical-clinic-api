package com.henrique.medical_clinic_api.features.doctor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
public class DoctorQueryFilter {
    private String name;
    private String crm;
    private String uf;

    public DoctorQueryFilter(String name, String crm, String uf) {
        this.name = name;
        this.crm = crm;
        this.uf = uf;
    }

    public Specification<Doctor> toSpecification() {
        return DoctorSpecification.startWithName(name)
                .and(DoctorSpecification.startWithCrm(crm))
                .and(DoctorSpecification.equalsUf(uf));
    }
}
