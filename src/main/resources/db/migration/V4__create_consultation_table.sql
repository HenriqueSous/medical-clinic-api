CREATE TABLE consultations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    consultation_date DATE NOT NULL,
    consultation_time TIME NOT NULL,
    status VARCHAR(15) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_consultation_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_consultation_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    UNIQUE KEY uk_doctor_schedule (doctor_id, consultation_date, consultation_time),
    INDEX idx_consultation_date (consultation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;