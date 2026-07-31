package com.matriculaonline.controller;

import com.matriculaonline.dto.request.AlunoRequest;
import com.matriculaonline.dto.response.AlunoResponse;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody AlunoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<AlunoResponse>> listar(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(alunoService.listar(q, pageable));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<AlunoResponse> buscarPorUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(alunoService.buscarPorUuid(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable UUID uuid,
                                                    @Valid @RequestBody AlunoRequest request) {
        return ResponseEntity.ok(alunoService.atualizar(uuid, request));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deletar(@PathVariable UUID uuid) {
        alunoService.deletar(uuid);
        return ResponseEntity.noContent().build();
    }
}
