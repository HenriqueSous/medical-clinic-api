package com.henrique.medical_clinic_api.features.util;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class ContainerIT {

    @ServiceConnection
    private static final MySQLContainer CONTAINER = new MySQLContainer(DockerImageName.parse("mysql:8.0"));

    static {
        CONTAINER.start();
    }
}
