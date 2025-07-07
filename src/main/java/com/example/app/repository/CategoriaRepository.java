package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.app.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Categorías generales (no asociadas a un usuario)
    @Query("SELECT c FROM Categoria c WHERE c.usuario IS NULL")
    List<Categoria> findAllGenerales();

    // Si quieres también obtener las del usuario:
    List<Categoria> findByUsuario_Id(Long usuarioId);
    

    
    @Query("""
    	    SELECT c FROM Categoria c
    	    WHERE c.usuario IS NULL OR c.usuario.id = :usuarioId
    	""")
    	List<Categoria> findCategoriasVisiblesParaUsuario(@Param("usuarioId") Long usuarioId);

}
