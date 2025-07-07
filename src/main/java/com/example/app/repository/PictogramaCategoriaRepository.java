package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;
import com.example.app.model.PictogramaCategoria;

import jakarta.transaction.Transactional;

public interface PictogramaCategoriaRepository extends JpaRepository<PictogramaCategoria, Long> {

    @Query("""
        SELECT pc FROM PictogramaCategoria pc
        WHERE pc.usuario.id = :usuarioId
    """)
    List<PictogramaCategoria> findAllByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("""
    	    SELECT pc FROM PictogramaCategoria pc
    	    LEFT JOIN PictogramaOculto po ON po.pictograma.id = pc.pictograma.id AND po.usuario.id = :usuarioId
    	    WHERE pc.categoria.id = :categoriaId
    	      AND (pc.usuario.id = :usuarioId)
    	      AND po.id IS NULL
    	""")
    	List<PictogramaCategoria> findByCategoriaIdAndUsuarioId(
    	    @Param("categoriaId") Long categoriaId,
    	    @Param("usuarioId") Long usuarioId
    	);

  
    @Query("""
        SELECT pc FROM PictogramaCategoria pc
        WHERE pc.pictograma.id = :pictogramaId AND pc.usuario.id = :usuarioId
    """)
    List<PictogramaCategoria> findByPictogramaIdAndUsuarioId(
        @Param("pictogramaId") Long pictogramaId,
        @Param("usuarioId") Long usuarioId
    );

    @Query("""
        SELECT pc FROM PictogramaCategoria pc
        WHERE pc.pictograma.id = :pictogramaId AND pc.categoria.id = :categoriaId AND pc.usuario.id = :usuarioId
    """)
    PictogramaCategoria findByPictogramaCategoriaUsuario(
        @Param("pictogramaId") Long pictogramaId,
        @Param("categoriaId") Long categoriaId,
        @Param("usuarioId") Long usuarioId
    );
    @Query("SELECT pc FROM PictogramaCategoria pc WHERE pc.usuario.id = :usuarioId")
    List<PictogramaCategoria> buscarPorUsuario(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("""
        DELETE FROM PictogramaCategoria pc
        WHERE pc.pictograma.id = :pictogramaId
        AND pc.categoria.id = :categoriaId
        AND pc.usuario.id = :usuarioId
    """)
    void eliminarRelacionPorUsuario(
        @Param("pictogramaId") Long pictogramaId,
        @Param("categoriaId") Long categoriaId,
        @Param("usuarioId") Long usuarioId
    );
    
    @Query("SELECT pc FROM PictogramaCategoria pc WHERE pc.usuario IS NULL")
    List<PictogramaCategoria> findAllGenerales();

    @Modifying
    @Query("DELETE FROM PictogramaCategoria pc WHERE pc.categoria.id = :categoriaId AND ( pc.usuario.id = :usuarioId)")
    void eliminarRelacionesPorCategoriaYUsuario(@Param("categoriaId") Long categoriaId, @Param("usuarioId") Long usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PictogramaCategoria pc WHERE pc.usuario.id = :usuarioId AND pc.pictograma.id = :pictogramaId")
    void eliminarPorUsuarioYPictograma(@Param("usuarioId") Long usuarioId, @Param("pictogramaId") Long pictogramaId);
    
    @Query("SELECT pc.categoria FROM PictogramaCategoria pc WHERE pc.pictograma.id = :pictogramaId AND pc.usuario.id = :usuarioId")
    List<Categoria> buscarCategoriasDePictogramaPorUsuario(@Param("pictogramaId") Long pictogramaId, @Param("usuarioId") Long usuarioId);

    @Query("""
    	    SELECT pc.pictograma FROM PictogramaCategoria pc
    	    LEFT JOIN PictogramaOculto po ON po.pictograma = pc.pictograma AND po.usuario.id = :usuarioId
    	    WHERE pc.categoria.id = :categoriaId
    	      AND pc.usuario.id = :usuarioId
    	      AND po.id IS NULL
    	""")
    	List<Pictograma> obtenerPictogramasDeCategoriaPorUsuario(
    	    @Param("categoriaId") Long categoriaId,
    	    @Param("usuarioId") Long usuarioId
    	);



}
