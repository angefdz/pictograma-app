package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.app.model.Configuracion;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {
    
	@Query("SELECT c FROM Configuracion c WHERE c.usuario.id = :usuarioId")
    Configuracion buscarPorUsuario(Long usuarioId);
}
