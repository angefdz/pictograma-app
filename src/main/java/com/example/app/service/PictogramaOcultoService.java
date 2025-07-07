package com.example.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.dto.CategoriaSimple;
import com.example.app.dto.PictogramaConCategorias;
import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;
import com.example.app.model.PictogramaOculto;
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaOcultoRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;

@Service
public class PictogramaOcultoService {

    @Autowired
    private PictogramaOcultoRepository repository;

    @Autowired
    private PictogramaRepository pictogramaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private PictogramaCategoriaRepository pictogramaCategoriaRepository;

    public boolean ocultarPictograma(Pictograma pictograma, Usuario usuario) {
        if (repository.existsByUsuarioIdAndPictogramaId(usuario.getId(), pictograma.getId())) {
            return false;
        }
        repository.save(new PictogramaOculto(usuario, pictograma));
        return true;
    }

    public boolean desocultarPictograma(Pictograma pictograma, Usuario usuario) {
        Optional<PictogramaOculto> oculto = repository.findByPictogramaIdAndUsuarioId(pictograma.getId(), usuario.getId());
        if (oculto.isPresent()) {
            repository.delete(oculto.get());
            return true;
        }
        return false;
    }

    public boolean ocultarPorIds(Long pictogramaId, Long usuarioId) {
        Optional<Pictograma> pictogramaOpt = pictogramaRepository.findById(pictogramaId);
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(usuarioId);
        if (pictogramaOpt.isEmpty() || usuarioOpt.isEmpty()) return false;
        return ocultarPictograma(pictogramaOpt.get(), usuarioOpt.get());
    }

    public boolean desocultarPorIds(Long pictogramaId, Long usuarioId) {
        Optional<Pictograma> pictogramaOpt = pictogramaRepository.findById(pictogramaId);
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(usuarioId);
        if (pictogramaOpt.isEmpty() || usuarioOpt.isEmpty()) return false;
        return desocultarPictograma(pictogramaOpt.get(), usuarioOpt.get());
    }

    public boolean estaOculto(Long pictogramaId, Long usuarioId) {
        return repository.existsByPictogramaIdAndUsuarioId(pictogramaId, usuarioId);
    }

    private PictogramaConCategorias convertirADTO(Pictograma p, Long usuarioId) {

        List<Categoria> categorias = pictogramaCategoriaRepository
            .buscarCategoriasDePictogramaPorUsuario(p.getId(), usuarioId);

        List<CategoriaSimple> categoriasDTO = new ArrayList<>();
        for (Categoria c : categorias) {
            Long usuarioIdCategoria = (c.getUsuario() != null) ? c.getUsuario().getId() : null;
            categoriasDTO.add(new CategoriaSimple(c.getId(), c.getNombre(), c.getImagen(), usuarioIdCategoria));
        }

        return new PictogramaConCategorias(
            p.getId(),
            p.getNombre(),
            p.getImagen(),
            p.getTipo(),
            usuarioId,
            categoriasDTO
        );
    }


    private List<PictogramaConCategorias> convertirListaADTO(List<Pictograma> lista, Long usuarioId) {
        List<PictogramaConCategorias> resultado = new ArrayList<>();
        for (Pictograma p : lista) {
            resultado.add(convertirADTO(p,usuarioId));
        }
        return resultado;
    }

    public List<PictogramaConCategorias> obtenerPictogramasOcultos(Long usuarioId) {
        List<PictogramaOculto> ocultos = repository.obtenerPictogramasOcultosPorUsuario(usuarioId);
        if (ocultos.isEmpty()) {
        	System.out.println("Estoy vaciooooo");
        }else {
        	System.out.println("no lo estoy");
        }
        List<Pictograma> pictogramas = new ArrayList<>();

        for (PictogramaOculto po : ocultos) {
            pictogramas.add(po.getPictograma());
        }
        
        return convertirListaADTO(pictogramas, usuarioId);
    }
}
