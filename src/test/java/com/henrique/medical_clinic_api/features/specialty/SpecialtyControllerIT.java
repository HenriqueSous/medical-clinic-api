package com.henrique.medical_clinic_api.features.specialty;

import com.henrique.medical_clinic_api.features.util.ContainerIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class SpecialtyControllerIT extends ContainerIT {

    @Autowired
    private RestTestClient client;

    @Autowired
    private SpecialtyRepository repository;

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void post_createSpecialty_WhenCorrectDataIsProvided() {
        SpecialtyRequestDTO request = new SpecialtyRequestDTO(
                "Neurologia",
                "Descrição Neurologia"
        );

        client.post()
                .uri("/specialties")
                .body(request)
                .exchange()

                .expectStatus()
                .isCreated()

                .expectBody()
                .jsonPath("$.name")
                .isEqualTo(request.name())
                .jsonPath("$.description")
                .isEqualTo(request.description())
                .jsonPath("$.doctors")
                .isEmpty();
    }

    @Test
    void post_ReturnBadRequestException_WhenSpecialtyAlreadyExists() {
        Specialty entity = specialtyMapper.toEntity(new SpecialtyRequestDTO(
                "Neurologia",
                "Descrição Neurologia"
        ));
        repository.save(entity);

        SpecialtyRequestDTO request = new SpecialtyRequestDTO(
                entity.getName(),
                entity.getDescription()
        );

        client.post()
                .uri("/specialties")
                .body(request)
                .exchange()

                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    ";Descrição Neurologia",
                    "Neurologia;",
                    ";",
                    "'';Descrição Neurologia",
                    "Neurologia;''",
                    "'';''"
            },
            delimiter = ';'
    )
    void post_ReturnBadRequestException_WhenDataIsNotProvidedCorrectly(String name, String description) {
        ObjectNode jsonNode = new ObjectMapper().createObjectNode();
        jsonNode.put("name", name);
        jsonNode.put("description", description);

        client.post()
                .uri("/specialties")
                .body(jsonNode)
                .exchange()

                .expectStatus()
                .isBadRequest();
    }

    @Test
    void get_ReturnsSpecialty_WhenIdIsFound() {
        Specialty entity = specialtyMapper.toEntity(new SpecialtyRequestDTO(
                "Neurologia",
                "Descrição Neurologia"
        ));

        repository.save(entity);

        client.get()
                .uri("/specialties/{id}", 1)
                .exchange()
                .expectStatus()
                .isOk()

                .expectBody()
                .jsonPath("$.name")
                .isEqualTo(entity.getName())
                .jsonPath("$.description")
                .isEqualTo(entity.getDescription())
                .jsonPath("$.doctors")
                .isEmpty();
    }
}
