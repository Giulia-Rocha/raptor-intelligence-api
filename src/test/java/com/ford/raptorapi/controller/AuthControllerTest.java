package com.ford.raptorapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.raptorapi.BaseTest;
import com.ford.raptorapi.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest extends BaseTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<String> login(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> body = new HttpEntity<>(
                json(Map.of("email", email, "password", password)), headers);
        return rest.postForEntity("/auth/login", body, String.class);
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void login_consultor_deve_retornar_token_com_role_consultor() {
        ResponseEntity<String> response = login("admin@ford.com.br", "password");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AuthResponse auth = parse(response.getBody());
        assertThat(auth.getToken()).isNotBlank();
        assertThat(auth.getTokenType()).isEqualTo("Bearer");
        assertThat(auth.getExpiresIn()).isEqualTo(86_400L);
        assertThat(auth.getRole()).isEqualTo("CONSULTOR");
    }

    @Test
    void login_admin_deve_retornar_role_admin() {
        ResponseEntity<String> response = login("admin@raptor.com.br", "admin123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AuthResponse auth = parse(response.getBody());
        assertThat(auth.getRole()).isEqualTo("ADMIN");
        assertThat(auth.getToken()).isNotBlank();
    }

    @Test
    void login_credenciais_invalidas_deve_retornar_401() {
        ResponseEntity<String> response = login("admin@ford.com.br", "senha-errada");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Invalid email or password");
    }

    @Test
    void login_payload_invalido_deve_retornar_400() {
        ResponseEntity<String> response = login("", "");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void me_com_token_valido_deve_retornar_perfil() {
        String token = parse(login("admin@ford.com.br", "password").getBody()).getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<Void> body = new HttpEntity<>(headers);

        ResponseEntity<String> response = rest.postForEntity("/auth/me", body, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("admin@ford.com.br", "CONSULTOR", "Ford Matriz");
    }

    @Test
    void me_sem_token_deve_retornar_401() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> body = new HttpEntity<>(headers);

        ResponseEntity<String> response = rest.postForEntity("/auth/me", body, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private AuthResponse parse(String body) {
        try {
            return objectMapper.readValue(body, AuthResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}