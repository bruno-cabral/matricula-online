package com.matriculaonline.repository;

import com.matriculaonline.domain.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByUuid(UUID uuid);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
