package com.ford.raptorapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.raptorapi.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileControllerTest extends BaseTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void detect_com_modelo_ranger_deve_retornar_perfil_entusiasta() throws Exception {
        String payload = "{\"brand\":\"Ford\",\"model\":\"Ranger\",\"version\":\"Raptor\",\"attributes\":[]}";

        ResponseEntity<String> response = rest.exchange(
                "/profiles/detect", HttpMethod.POST,
                jsonEntity(payload), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("enthusiast", "Entusiasta Off-Road");
        assertThat(response.getBody()).contains("Desempenho de referência");
    }

    @Test
    void detect_sem_dados_deve_retornar_perfil_default_tech() throws Exception {
        ResponseEntity<String> response = rest.exchange(
                "/profiles/detect", HttpMethod.POST, jsonEntity("{}"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("tech", "Entusiasta de Tecnologia");
    }

    @Test
    void get_by_type_deve_retornar_perfil_com_argumentos() {
        ResponseEntity<String> response = rest.getForEntity("/profiles/rational", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("rational", "detectedSignals", "salesArguments");
    }

    @Test
    void get_by_type_inexistente_deve_retornar_404() {
        ResponseEntity<String> response = rest.getForEntity("/profiles/inexistente", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("não encontrado");
    }

    private HttpEntity<String> jsonEntity(String payload) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(payload, headers);
    }
}