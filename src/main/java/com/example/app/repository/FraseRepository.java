package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.app.model.Frase;

public interface FraseRepository extends JpaRepository<Frase, Long> {
    
	@Query("SELECT f FROM Frase f WHERE f.usuario.id = :usuarioId")
    List<Frase> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
