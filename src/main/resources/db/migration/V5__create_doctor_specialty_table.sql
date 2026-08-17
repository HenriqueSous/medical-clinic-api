CREATE TABLE doctor_specialty (
    doctor_id BIGINT NOT NULL,
    specialty_id BIGINT NOT NULL,
    PRIMARY KEY (doctor_id, specialty_id),
    CONSTRAINT fk_doctor_id
        FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_specialty_id
        FOREIGN KEY (specialty_id) REFERENCES specialties (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;