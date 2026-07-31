package com.matriculaonline.repository;

import com.matriculaonline.domain.model.Curso;
import org.springframework.data.jpa.domain.Specification;

public final class CursoSpecifications {

    private CursoSpecifications() {
    }

    public static Specification<Curso> comBusca(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }
            String termo = "%" + q.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("nome")), termo);
        };
    }
}
