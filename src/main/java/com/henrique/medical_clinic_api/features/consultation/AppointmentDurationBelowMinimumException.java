package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.features.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AppointmentDurationBelowMinimumException extends BusinessException {
    public AppointmentDurationBelowMinimumException(int duration, int minimumDuration) {
        super("The provided duration "+duration+" is below the required minimum duration "+minimumDuration,
                HttpStatus.BAD_REQUEST);
    }
}
