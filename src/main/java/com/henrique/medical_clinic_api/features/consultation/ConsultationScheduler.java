package com.henrique.medical_clinic_api.features.consultation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ConsultationScheduler {
    @Autowired
    ConsultationService consultationService;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void executeCancelOverdueAppointments() {
        consultationService.cancelOverdueAppointments();
    }
}
