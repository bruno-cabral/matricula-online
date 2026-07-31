package com.matriculaonline.repository;

import com.matriculaonline.domain.model.StatusTurma;
import com.matriculaonline.domain.model.Turma;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class TurmaSpecifications {

    private TurmaSpecifications() {
    }

    public static Specification<Turma> comFiltros(StatusTurma status, Boolean lotada, String q) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (Boolean.TRUE.equals(lotada)) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("vagasOcupadas"), root.get("vagas")));
            }

            if (q != null && !q.isBlank()) {
                String termo = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("codigo")), termo),
                        cb.like(cb.lower(root.get("professor")), termo),
                        cb.like(cb.lower(root.get("disciplina").get("nome")), termo)
                ));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
