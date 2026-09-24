package com.ford.raptorapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.raptorapi.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CompareControllerTest extends BaseTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void compare_com_ids_validos_deve_retornar_200() {
        Integer idA = vehicleId(0);
        Integer idB = vehicleId(1);

        ResponseEntity<String> response = rest.getForEntity(
                "/compare?ids=" + idA + "&ids=" + idB, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("vehicles", "categories");
    }

    @Test
    void compare_sem_ids_deve_retornar_400() {
        ResponseEntity<String> response = rest.getForEntity("/compare", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void compare_com_id_inexistente_deve_retornar_404() {
        ResponseEntity<String> response = rest.getForEntity(
                "/compare?ids=999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("não encontrado");
    }

    @Test
    void compare_deve_incluir_valores_numericos_de_desempenho() throws Exception {
        Integer idA = vehicleId(0);
        Integer idB = vehicleId(1);

        ResponseEntity<String> response = rest.getForEntity(
                "/compare?ids=" + idA + "&ids=" + idB, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode nodes = objectMapper.readTree(response.getBody());
        String accelerationA = nodes.path("categories").path("desempenho")
                .path("specs").get(0).path("values").path(idA.toString()).asText();
        assertThat(accelerationA)
                .withFailMessage("Aceleração do veículo de referência não deveria estar vazia")
                .isNotEqualTo("-");
    }

    private Integer vehicleId(int index) {
        try {
            ResponseEntity<String> response = rest.getForEntity("/vehicles", String.class);
            return objectMapper.readTree(response.getBody()).get(index).get("id").asInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}