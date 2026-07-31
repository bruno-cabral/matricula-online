package com.matriculaonline.repository;

import com.matriculaonline.domain.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long>, JpaSpecificationExecutor<Disciplina> {
    Optional<Disciplina> findByUuid(UUID uuid);

    boolean existsByCursoUuid(UUID cursoUuid);
}
