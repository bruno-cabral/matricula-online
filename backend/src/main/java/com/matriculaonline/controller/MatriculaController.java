package com.matriculaonline.controller;

import com.matriculaonline.domain.model.StatusMatricula;
import com.matriculaonline.dto.request.MatriculaRequest;
import com.matriculaonline.dto.response.MatriculaResponse;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    public ResponseEntity<MatriculaResponse> criar(@Valid @RequestBody MatriculaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaService.criar(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MatriculaResponse>> listar(
            @RequestParam(required = false) StatusMatricula status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(matriculaService.listar(status, pageable));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<MatriculaResponse> buscarPorUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(matriculaService.buscarPorUuid(uuid));
    }

    @PatchMapping("/{uuid}/confirmar")
    public ResponseEntity<MatriculaResponse> confirmar(@PathVariable UUID uuid) {
        return ResponseEntity.ok(matriculaService.confirmar(uuid));
    }

    @PatchMapping("/{uuid}/cancelar")
    public ResponseEntity<MatriculaResponse> cancelar(@PathVariable UUID uuid) {
        return ResponseEntity.ok(matriculaService.cancelar(uuid));
    }

    @GetMapping("/aluno/{alunoUuid}")
    public ResponseEntity<PageResponse<MatriculaResponse>> listarPorAluno(
            @PathVariable UUID alunoUuid,
            @RequestParam(required = false) StatusMatricula status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(matriculaService.listarPorAluno(alunoUuid, status, pageable));
    }

    @GetMapping("/turma/{turmaUuid}")
    public ResponseEntity<PageResponse<MatriculaResponse>> listarPorTurma(
            @PathVariable UUID turmaUuid,
            @RequestParam(required = false) StatusMatricula status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(matriculaService.listarPorTurma(turmaUuid, status, pageable));
    }
}
