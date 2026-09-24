package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.request.SaveComparisonRequest;
import com.ford.raptorapi.dto.response.ComparisonResponse;
import com.ford.raptorapi.model.AppUser;
import com.ford.raptorapi.service.ComparisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comparisons")
@RequiredArgsConstructor
@Tag(name = "Comparações", description = "Histórico de comparações do usuário autenticado. "
        + "Posse de dados: cada usuário só acessa as próprias comparações (403 se não for o dono).")
public class ComparisonController {

    private final ComparisonService comparisonService;

    @GetMapping
    @Operation(summary = "Lista minhas comparações")
    public ResponseEntity<List<ComparisonResponse>> getMyComparisons(@AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(comparisonService.findByUser(user.getId()));
    }

    @PostMapping
    @Operation(summary = "Salva uma comparação")
    public ResponseEntity<ComparisonResponse> saveComparison(
            @AuthenticationPrincipal AppUser user,
            @Valid @RequestBody SaveComparisonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comparisonService.save(user.getId(), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma de minhas comparações", description = "403 se a comparação não pertence ao usuário.")
    public ResponseEntity<Void> deleteComparison(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Integer id) {
        comparisonService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
