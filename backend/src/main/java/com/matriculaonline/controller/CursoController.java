package com.matriculaonline.controller;

import com.matriculaonline.dto.request.CursoRequest;
import com.matriculaonline.dto.response.CursoResponse;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    public ResponseEntity<CursoResponse> criar(@Valid @RequestBody CursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CursoResponse>> listar(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(cursoService.listar(q, pageable));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CursoResponse> buscarPorUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(cursoService.buscarPorUuid(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<CursoResponse> atualizar(@PathVariable UUID uuid,
                                                    @Valid @RequestBody CursoRequest request) {
        return ResponseEntity.ok(cursoService.atualizar(uuid, request));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deletar(@PathVariable UUID uuid) {
        cursoService.deletar(uuid);
        return ResponseEntity.noContent().build();
    }
}
