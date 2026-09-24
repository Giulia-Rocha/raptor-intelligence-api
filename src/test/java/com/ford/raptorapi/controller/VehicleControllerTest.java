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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleControllerTest extends BaseTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<String> tokenLogin(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> body = new HttpEntity<>(
                json(Map.of("email", email, "password", password)), headers);
        return rest.postForEntity("/auth/login", body, String.class);
    }

    private String bearer(String email, String password) {
        String body = tokenLogin(email, password).getBody();
        try {
            return "Bearer " + objectMapper.readTree(body).get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Integer firstVehicleId() {
        ResponseEntity<String> response = rest.getForEntity("/vehicles", String.class);
        try {
            return objectMapper.readTree(response.getBody()).get(0).get("id").asInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Integer firstBrandId() {
        ResponseEntity<String> response = rest.getForEntity("/brands", String.class);
        try {
            return objectMapper.readTree(response.getBody()).get(0).get("id").asInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void list_vehicles_publico_deve_retornar_200() {
        ResponseEntity<String> response = rest.getForEntity("/vehicles", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("model");
    }

    @Test
    void list_vehicles_deve_retornar_lista() {
        ResponseEntity<String> response = rest.getForEntity("/vehicles", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        try {
            assertThat(objectMapper.readTree(response.getBody()).size()).isPositive();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void get_vehicle_inexistente_deve_retornar_404() {
        ResponseEntity<String> response = rest.getForEntity("/vehicles/999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("not found");
    }

    @Test
    void get_vehicle_com_id_invalido_deve_retornar_400() {
        ResponseEntity<String> response = rest.getForEntity("/vehicles/abc", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("invalid");
    }

    @Test
    void create_vehicle_sem_token_deve_retornar_401() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> body = new HttpEntity<>(
                json(vehiclePayload("Teste 401")), headers);

        ResponseEntity<String> response = rest.postForEntity("/vehicles", body, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void create_vehicle_com_consultor_deve_retornar_403() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, bearer("admin@ford.com.br", "password"));
        HttpEntity<String> body = new HttpEntity<>(
                json(vehiclePayload("Teste 403")), headers);

        ResponseEntity<String> response = rest.postForEntity("/vehicles", body, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void create_vehicle_com_admin_deve_retornar_201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, bearer("admin@raptor.com.br", "admin123"));
        HttpEntity<String> body = new HttpEntity<>(
                json(vehiclePayload("Teste Criado")), headers);

        ResponseEntity<String> response = rest.postForEntity("/vehicles", body, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("Teste Criado");
    }

    @Test
    void delete_vehicle_com_admin_deve_retornar_204() {
        Integer createdId = persistVehicle("Para Deletar");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, bearer("admin@raptor.com.br", "admin123"));

        ResponseEntity<String> response = rest.exchange(
                "/vehicles/" + createdId, HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void delete_vehicle_com_consultor_deve_retornar_403() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, bearer("admin@ford.com.br", "password"));

        ResponseEntity<String> response = rest.exchange(
                "/vehicles/1", HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private Map<String, Object> vehiclePayload(String model) {
        return Map.of(
                "brandId", firstBrandId(),
                "model", model,
                "version", "Teste V1",
                "fuelType", "gasolina",
                "category", "desert_runner");
    }

    private Integer persistVehicle(String model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, bearer("admin@raptor.com.br", "admin123"));
        HttpEntity<String> body = new HttpEntity<>(
                json(vehiclePayload(model)), headers);

        ResponseEntity<String> response = rest.postForEntity("/vehicles", body, String.class);
        try {
            return objectMapper.readTree(response.getBody()).get("id").asInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}