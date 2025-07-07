package com.example.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.app.model.Usuario;
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{

	@Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)")
	Optional<Usuario> buscarPorEmail(@Param("email") String email);

	@Query("SELECT u FROM Usuario u WHERE u.id = :id")
    Optional<Usuario> buscarPorId(@Param("id") Long id);
}
