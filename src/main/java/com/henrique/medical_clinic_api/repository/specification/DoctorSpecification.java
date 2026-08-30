package com.henrique.medical_clinic_api.repository.specification;

import com.henrique.medical_clinic_api.model.Doctor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

public class DoctorSpecification {
    public static Specification<Doctor> startWithName(String name) {
        return ((root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(name)) return null;
            return criteriaBuilder.like(root.get("name"), name + "%");
        });
    }

    public static Specification<Doctor> startWithCrm(String crm) {
        return ((root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(crm)) return null;
            return criteriaBuilder.like(root.get("crm"), crm + "%");
        });
    }

    public static Specification<Doctor> equalsUf(String uf) {
        return ((root, query, criteriaBuilder) -> {
            if (ObjectUtils.isEmpty(uf)) return null;
            return criteriaBuilder.equal(root.get("uf"), uf);
        });
    }
}
