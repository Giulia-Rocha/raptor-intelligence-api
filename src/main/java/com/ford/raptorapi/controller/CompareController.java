package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.response.CompareResponse;
import com.ford.raptorapi.service.CompareService;
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
public class CompareController {

    private final CompareService compareService;

    @GetMapping
    public ResponseEntity<CompareResponse> compare(
            @RequestParam List<Integer> ids,
            @RequestParam(required = false) List<String> categories
    ) {
        return ResponseEntity.ok(compareService.compare(ids, categories));
    }
}
