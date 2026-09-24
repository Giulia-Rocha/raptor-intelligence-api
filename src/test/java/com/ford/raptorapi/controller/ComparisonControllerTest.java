package com.ford.raptorapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.raptorapi.BaseTest;
import com.ford.raptorapi.model.AppUser;
import com.ford.raptorapi.model.enums.UserRole;
import com.ford.raptorapi.repository.AppUserRepository;
import com.ford.raptorapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ComparisonControllerTest extends BaseTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Integer vehicleId(int index) {
        try {
            ResponseEntity<String> response = rest.getForEntity("/vehicles", String.class);
            return objectMapper.readTree(response.getBody()).get(index).get("id").asInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String login(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> body = new HttpEntity<>(
                json(Map.of("email", email, "password", password)), headers);
        try {
            ResponseEntity<String> response = rest.postForEntity("/auth/login", body, String.class);
            return "Bearer " + objectMapper.readTree(response.getBody()).get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int saveComparison(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, token);
        HttpEntity<String> body = new HttpEntity<>(
                json(Map.of(
                        "vehicleAId", vehicleId(0),
                        "vehicleBId", vehicleId(1),
                        "notes", "teste")), headers);

        ResponseEntity<String> response = rest.postForEntity("/comparisons", body, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        try {
            return objectMapper.readTree(response.getBody()).get("id").asInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void list_sem_token_deve_retornar_401() {
        ResponseEntity<String> response = rest.getForEntity("/comparisons", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void save_list_e_delete_com_token_deve_funcionar() {
        String token = login("admin@ford.com.br", "password");

        int comparisonId = saveComparison(token);

        ResponseEntity<String> listResponse = rest.exchange("/comparisons", HttpMethod.GET,
                new HttpEntity<>(headers(token)), String.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("\"id\":");

        ResponseEntity<String> deleteResponse = rest.exchange(
                "/comparisons/" + comparisonId, HttpMethod.DELETE,
                new HttpEntity<>(headers(token)), String.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void save_comparison_sem_token_deve_retornar_401() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> body = new HttpEntity<>(
                json(Map.of(
                        "vehicleAId", vehicleId(0),
                        "vehicleBId", vehicleId(1))), headers);

        ResponseEntity<String> response = rest.postForEntity("/comparisons", body, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void save_comparison_com_veiculo_inexistente_deve_retornar_404() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, login("admin@ford.com.br", "password"));
        HttpEntity<String> body = new HttpEntity<>(
                json(Map.of(
                        "vehicleAId", 999999,
                        "vehicleBId", vehicleId(0))), headers);

        ResponseEntity<String> response = rest.postForEntity("/comparisons", body, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("not found");
    }

    @Test
    void delete_comparison_de_outro_usuario_deve_retornar_403() {
        String donoToken = login("admin@ford.com.br", "password");
        int comparisonId = saveComparison(donoToken);

        AppUser outro = new AppUser();
        outro.setName("Outro Consultor");
        outro.setEmail("outro@ford.com.br");
        outro.setPassword(passwordEncoder.encode("password"));
        outro.setDealership("Ford Teste");
        outro.setRole(UserRole.CONSULTOR);
        outro = userRepository.save(outro);

        String outroToken = "Bearer " + jwtService.generateToken(outro);

        ResponseEntity<String> deleteResponse = rest.exchange(
                "/comparisons/" + comparisonId, HttpMethod.DELETE,
                new HttpEntity<>(headers(outroToken)), String.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpHeaders headers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);
        return headers;
    }
}