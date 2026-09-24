package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.response.CompareResponse;
import com.ford.raptorapi.service.CompareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/compare")
@RequiredArgsConstructor
@Tag(name = "Comparativo", description = "Comparação dinâmica entre veículos")
public class CompareController {

    private final CompareService compareService;

    @Operation(summary = "Compara veículos por ids",
            description = "Monta o comparativo dinamicamente a partir da tabela spec_category_map. "
                    + "Sem 'ids' retorna 400; ids inexistentes retornam 404.")
    @GetMapping
    public ResponseEntity<CompareResponse> compare(
            @RequestParam List<Integer> ids,
            @RequestParam(required = false) List<String> categories
    ) {
        return ResponseEntity.ok(compareService.compare(ids, categories));
    }
}
