package com.ford.raptorapi.security;

import com.ford.raptorapi.model.AppUser;
import com.ford.raptorapi.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "segredo-de-teste-com-pelo-menos-256-bits-para-hmac-sha");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600_000L);
    }

    private AppUser user(Integer id, String email, UserRole role) {
        AppUser user = new AppUser();
        user.setId(id);
        user.setEmail(email);
        user.setName("Teste");
        user.setRole(role);
        return user;
    }

    @Test
    void generateToken_deve_conter_claims_de_usuario() {
        AppUser user = user(42, "consultor@ford.com.br", UserRole.CONSULTOR);

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("consultor@ford.com.br");
        assertThat(jwtService.extractUserId(token)).isEqualTo(42);
        assertThat(jwtService.extractRole(token)).isEqualTo("CONSULTOR");
    }

    @Test
    void generateToken_deve_incluir_perfil_admin_e_expirar_apos_ttl() {
        AppUser admin = user(7, "admin@raptor.com.br", UserRole.ADMIN);

        String token = jwtService.generateToken(admin);

        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
        assertThat(jwtService.isTokenValid(token, admin)).isTrue();
        assertThat(jwtService.getExpirationMillis()).isEqualTo(3_600_000L);
    }

    @Test
    void isTokenValid_deve_rejeitar_token_para_outro_usuario() {
        AppUser dono = user(1, "a@ford.com.br", UserRole.CONSULTOR);
        AppUser outro = user(2, "b@ford.com.br", UserRole.CONSULTOR);

        String token = jwtService.generateToken(dono);

        assertThat(jwtService.isTokenValid(token, outro)).isFalse();
    }
}