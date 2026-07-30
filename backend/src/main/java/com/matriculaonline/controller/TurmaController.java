package com.matriculaonline.controller;

import com.matriculaonline.domain.model.StatusTurma;
import com.matriculaonline.dto.request.TurmaRequest;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.dto.response.TurmaResponse;
import com.matriculaonline.service.TurmaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/turmas")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    public ResponseEntity<TurmaResponse> criar(@Valid @RequestBody TurmaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turmaService.criar(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TurmaResponse>> listar(
            @RequestParam(required = false) StatusTurma status,
            @RequestParam(required = false) Boolean lotada,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(turmaService.listar(status, lotada, pageable));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TurmaResponse> buscarPorUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(turmaService.buscarPorUuid(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<TurmaResponse> atualizar(@PathVariable UUID uuid,
                                                    @Valid @RequestBody TurmaRequest request) {
        return ResponseEntity.ok(turmaService.atualizar(uuid, request));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deletar(@PathVariable UUID uuid) {
        turmaService.deletar(uuid);
        return ResponseEntity.noContent().build();
    }
}
