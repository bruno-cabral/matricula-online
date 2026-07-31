package com.matriculaonline.repository;

import com.matriculaonline.domain.model.Disciplina;
import org.springframework.data.jpa.domain.Specification;

public final class DisciplinaSpecifications {

    private DisciplinaSpecifications() {
    }

    public static Specification<Disciplina> comBusca(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }
            String termo = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("nome")), termo),
                    cb.like(cb.lower(root.get("curso").get("nome")), termo)
            );
        };
    }
}
