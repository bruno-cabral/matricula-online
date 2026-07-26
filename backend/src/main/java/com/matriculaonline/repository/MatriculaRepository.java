package com.matriculaonline.repository;

import com.matriculaonline.domain.model.Matricula;
import com.matriculaonline.domain.model.StatusMatricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByUuid(UUID uuid);

    boolean existsByAlunoIdAndTurmaIdAndStatusIn(Long alunoId, Long turmaId, List<StatusMatricula> statuses);

    Page<Matricula> findByAlunoUuid(UUID alunoUuid, Pageable pageable);

    Page<Matricula> findByAlunoUuidAndStatus(UUID alunoUuid, StatusMatricula status, Pageable pageable);

    Page<Matricula> findByTurmaUuid(UUID turmaUuid, Pageable pageable);

    Page<Matricula> findByTurmaUuidAndStatus(UUID turmaUuid, StatusMatricula status, Pageable pageable);

    Page<Matricula> findByStatus(StatusMatricula status, Pageable pageable);
}
