package com.matriculaonline.controller;

import com.matriculaonline.dto.request.DisciplinaRequest;
import com.matriculaonline.dto.response.DisciplinaResponse;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.service.DisciplinaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    @PostMapping
    public ResponseEntity<DisciplinaResponse> criar(@Valid @RequestBody DisciplinaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplinaService.criar(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<DisciplinaResponse>> listar(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(disciplinaService.listar(q, pageable));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<DisciplinaResponse> buscarPorUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(disciplinaService.buscarPorUuid(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<DisciplinaResponse> atualizar(@PathVariable UUID uuid,
                                                         @Valid @RequestBody DisciplinaRequest request) {
        return ResponseEntity.ok(disciplinaService.atualizar(uuid, request));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deletar(@PathVariable UUID uuid) {
        disciplinaService.deletar(uuid);
        return ResponseEntity.noContent().build();
    }
}
