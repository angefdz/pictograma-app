package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;

public interface PictogramaRepository extends JpaRepository<Pictograma, Long> {

    // Pictogramas generales (los que no tienen usuario asociado)
    @Query("SELECT p FROM Pictograma p WHERE p.usuario IS NULL")
    List<Pictograma> findAllGenerales();

    // Pictogramas personalizados de un usuario concreto
    @Query("SELECT p FROM Pictograma p WHERE p.usuario.id = :usuarioId")
    List<Pictograma> findAllPersonalizados(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT p FROM Pictograma p WHERE p.id IN :ids")
    List<Pictograma> buscarPorIds(@Param("ids") List<Long> ids);
    
    @Query("SELECT p FROM Pictograma p WHERE p.id = :id")
    Pictograma buscarPorId(@Param("id") Long id);
    

    @Query("""
    	    SELECT p FROM Pictograma p
    	    LEFT JOIN PictogramaOculto po ON po.pictograma.id = p.id AND po.usuario.id = :usuarioId
    	    WHERE (p.usuario IS NULL OR p.usuario.id = :usuarioId)
    	    AND po.id IS NULL
    	""")
    	List<Pictograma> findPictogramasVisiblesParaUsuario(@Param("usuarioId") Long usuarioId);

	Optional<Pictograma> findByUsuarioIsNull();
	
	@Query("SELECT COUNT(p) > 0 FROM Pictograma p WHERE LOWER(p.nombre) = LOWER(:nombre) AND p.usuario IS NULL")
	boolean existsPictogramaGeneralByNombre(@Param("nombre") String nombre);


 

}
