package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.model.PictogramaOculto;

@Repository
public interface PictogramaOcultoRepository extends JpaRepository<PictogramaOculto, Long> {

    @Query("SELECT p FROM PictogramaOculto p WHERE p.usuario.id = :idUsuario")
    List<PictogramaOculto> findByUsuarioId(@Param("idUsuario") Long idUsuario);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PictogramaOculto p WHERE p.usuario.id = :usuarioId AND p.pictograma.id = :pictogramaId")
    boolean existsByUsuarioIdAndPictogramaId(@Param("usuarioId") Long usuarioId, @Param("pictogramaId") Long pictogramaId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PictogramaOculto p WHERE p.usuario.id = :usuarioId AND p.pictograma.id = :pictogramaId")
    void deleteByUsuarioIdAndPictogramaId(@Param("usuarioId") Long usuarioId, @Param("pictogramaId") Long pictogramaId);

    @Query("SELECT p FROM PictogramaOculto p WHERE p.pictograma.id = :idPictograma AND p.usuario.id = :idUsuario")
    Optional<PictogramaOculto> findByPictogramaIdAndUsuarioId(@Param("idPictograma") Long idPictograma, @Param("idUsuario") Long idUsuario);
    
    boolean existsByPictogramaIdAndUsuarioId(Long pictogramaId, Long usuarioId);
    
    @Query("SELECT po FROM PictogramaOculto po WHERE po.usuario.id = :idUsuario")
    List<PictogramaOculto> obtenerPictogramasOcultosPorUsuario(@Param("idUsuario") Long usuarioId);



}
