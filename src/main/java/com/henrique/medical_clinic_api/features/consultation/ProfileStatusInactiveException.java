package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProfileStatusInactiveException extends BusinessException {
    public ProfileStatusInactiveException(String profile, long profileId) {
        super(String.format("%s with ID %d is inactive", profile, profileId), HttpStatus.BAD_REQUEST);
    }
}
