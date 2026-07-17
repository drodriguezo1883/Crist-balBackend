package com.cristobal.backend.repository;

import com.cristobal.backend.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
    boolean existsByCodigo(String codigo);
}