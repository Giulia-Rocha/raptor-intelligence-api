package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.request.SaveComparisonRequest;
import com.ford.raptorapi.dto.response.ComparisonResponse;
import com.ford.raptorapi.model.AppUser;
import com.ford.raptorapi.service.ComparisonService;
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
public class ComparisonController {

    private final ComparisonService comparisonService;

    @GetMapping
    public ResponseEntity<List<ComparisonResponse>> getMyComparisons(@AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(comparisonService.findByUser(user.getId()));
    }

    @PostMapping
    public ResponseEntity<ComparisonResponse> saveComparison(
            @AuthenticationPrincipal AppUser user,
            @Valid @RequestBody SaveComparisonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comparisonService.save(user.getId(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComparison(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Integer id) {
        comparisonService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
