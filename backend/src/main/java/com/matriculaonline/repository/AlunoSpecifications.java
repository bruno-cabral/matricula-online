package com.matriculaonline.repository;

import com.matriculaonline.domain.model.Aluno;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class AlunoSpecifications {

    private AlunoSpecifications() {
    }

    public static Specification<Aluno> comBusca(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }

            String termo = "%" + q.trim().toLowerCase() + "%";
            String digitos = q.replaceAll("\\D", "");

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("nome")), termo));
            predicates.add(cb.like(cb.lower(root.get("email")), termo));
            if (!digitos.isBlank()) {
                predicates.add(cb.like(root.get("cpf"), "%" + digitos + "%"));
            }

            return cb.or(predicates.toArray(Predicate[]::new));
        };
    }
}
