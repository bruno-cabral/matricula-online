package com.matriculaonline.repository;

import com.matriculaonline.domain.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long>, JpaSpecificationExecutor<Turma> {
    Optional<Turma> findByUuid(UUID uuid);
    boolean existsByCodigo(String codigo);

    boolean existsByDisciplinaUuid(UUID disciplinaUuid);
}
