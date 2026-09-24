package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.response.BrandResponse;
import com.ford.raptorapi.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@Tag(name = "Marcas", description = "Consulta pública de marcas")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(summary = "Lista as marcas dos veículos do catálogo")
    public ResponseEntity<List<BrandResponse>> listAll() {
        return ResponseEntity.ok(brandService.listAll());
    }
}