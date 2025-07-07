package com.example.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;
import com.example.app.model.PictogramaCategoria;
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaRepository;

import jakarta.transaction.Transactional;

@Service
public class PictogramaCategoriaService {

    @Autowired
    private PictogramaCategoriaRepository pictogramaCategoriaRepository;

    @Autowired
    private PictogramaRepository pictogramaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public void asignarPictogramaACategoria(Long pictogramaId, Long categoriaId, Usuario usuario) {
        Pictograma pictograma = pictogramaRepository.findById(pictogramaId)
            .orElseThrow(() -> new RuntimeException("Pictograma no encontrado"));

        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        PictogramaCategoria relacion = new PictogramaCategoria();
        relacion.setPictograma(pictograma);
        relacion.setCategoria(categoria);
        relacion.setUsuario(usuario);

        pictogramaCategoriaRepository.save(relacion);
    }

    @Transactional
    public void eliminarRelacion(Long pictogramaId, Long categoriaId, Usuario usuario) {
        pictogramaCategoriaRepository.eliminarRelacionPorUsuario(
            pictogramaId, categoriaId, usuario.getId()
        );
    }

    public List<PictogramaCategoria> obtenerRelacionesDeUsuario(Usuario usuario) {
        return pictogramaCategoriaRepository.buscarPorUsuario(usuario.getId());
    }
}
