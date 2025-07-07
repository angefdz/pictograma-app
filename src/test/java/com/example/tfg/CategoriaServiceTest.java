package com.example.tfg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.app.dto.CategoriaConPictogramas;
import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;
import com.example.app.model.PictogramaCategoria;
import com.example.app.model.PictogramaOculto;
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaOcultoRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;
import com.example.app.service.CategoriaService;
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @InjectMocks
    private CategoriaService categoriaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private PictogramaRepository pictogramaRepository;

    @Mock
    private PictogramaCategoriaRepository pictogramaCategoriaRepository;
    @Mock
    private PictogramaOcultoRepository pictogramaOcultoRepository;

    @Test
    void eliminarCategoria_siUsuarioEsDueño_eliminaCorrectamente() {
        Long categoriaId = 1L;
        Long usuarioId = 10L;

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Categoria categoria = new Categoria();
        categoria.setUsuario(usuario);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));

        // No se espera excepción, solo se verifica la llamada al repositorio
        categoriaService.eliminarCategoria(categoriaId, usuarioId);

        verify(categoriaRepository).delete(categoria);
    }

    
    @Test
    void eliminarCategoria_siUsuarioNoEsDueño_lanzaExcepcionForbidden() {
        Long categoriaId = 1L;
        Long usuarioId = 10L;
        Long otroUsuarioId = 99L;

        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(otroUsuarioId);

        Categoria categoria = new Categoria();
        categoria.setUsuario(otroUsuario);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));

        CategoriaService.AccesoProhibidoException exception = assertThrows(
            CategoriaService.AccesoProhibidoException.class,
            () -> categoriaService.eliminarCategoria(categoriaId, usuarioId)
        );

        assertEquals("No tienes permiso para eliminar esta categoría", exception.getMessage());
        verify(categoriaRepository, never()).delete(any());
    }



    @Test
    void obtenerCategoriasConPictogramasVisibles_conUsuarioValido_devuelveCategoriasConPictosVisibles() {
        Long usuarioId = 1L;

        Categoria categoria = new Categoria();
        categoria.setNombre("Animales");
        categoria.setImagen("img.png");

        Pictograma pictograma = new Pictograma();
        pictograma.setNombre("Gato");
        pictograma.setImagen("gato.png");
        pictograma.setTipo("sustantivo");

        PictogramaCategoria relacion = new PictogramaCategoria();
        relacion.setCategoria(categoria);
        relacion.setPictograma(pictograma);

        when(categoriaRepository.findCategoriasVisiblesParaUsuario(usuarioId))
            .thenReturn(List.of(categoria));

        when(pictogramaCategoriaRepository.findByCategoriaIdAndUsuarioId(categoria.getId(), usuarioId))
            .thenReturn(List.of(relacion));

        when(pictogramaOcultoRepository.findByUsuarioId(usuarioId))
            .thenReturn(List.of()); // No hay ocultos

        List<CategoriaConPictogramas> resultado = categoriaService.obtenerCategoriasConPictogramasVisibles(usuarioId);

        assertEquals(1, resultado.size());
        CategoriaConPictogramas catResultado = resultado.get(0);
        assertEquals("Animales", catResultado.getNombre());
        assertEquals(1, catResultado.getPictogramas().size());
        assertEquals("Gato", catResultado.getPictogramas().get(0).getNombre());
    }

    @Test
    void obtenerCategoriasConPictogramasVisibles_conPictogramasOcultos_noIncluyePictosOcultos() {
        Long usuarioId = 1L;

        Categoria categoria = new Categoria();
        categoria.setNombre("Saludo");

        Pictograma pictograma = new Pictograma();
        pictograma.setNombre("Hola");

        PictogramaCategoria relacion = new PictogramaCategoria();
        relacion.setCategoria(categoria);
        relacion.setPictograma(pictograma);

        PictogramaOculto oculto = new PictogramaOculto();
        oculto.setPictograma(pictograma);

        when(categoriaRepository.findCategoriasVisiblesParaUsuario(usuarioId))
            .thenReturn(List.of(categoria));

        when(pictogramaCategoriaRepository.findByCategoriaIdAndUsuarioId(categoria.getId(), usuarioId))
            .thenReturn(List.of(relacion));

        when(pictogramaOcultoRepository.findByUsuarioId(usuarioId))
            .thenReturn(List.of(oculto)); // pictograma oculto

        List<CategoriaConPictogramas> resultado = categoriaService.obtenerCategoriasConPictogramasVisibles(usuarioId);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getPictogramas().isEmpty()); // no devuelve el pictograma oculto
    }


}
