package com.example.tfg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional; // ✅

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.app.dto.PictogramaConCategorias;
import com.example.app.dto.PictogramaConCategoriasInput;
import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;
import com.example.app.model.PictogramaCategoria;
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;
import com.example.app.service.PictogramaService;
import com.example.app.service.PictogramaService.AccesoPictogramaDenegadoException;
import com.example.app.service.PictogramaService.PictogramaNoEncontradoException;

//... (imports y anotaciones igual que los tuyos)

@ExtendWith(MockitoExtension.class)
class PictogramaServiceTest {

 @InjectMocks
 private PictogramaService pictogramaService;

 @Mock
 private PictogramaRepository pictogramaRepository;

 @Mock
 private UsuarioRepository usuarioRepository;

 @Mock
 private CategoriaRepository categoriaRepository;

 @Mock
 private PictogramaCategoriaRepository pictogramaCategoriaRepository;

 @Test
 void crearDesdeInputDTO_conUsuarioYCategoriasValidas_creaCorrectamente() {
     Long usuarioId = 1L;
     Long categoriaId = 100L;

     PictogramaConCategoriasInput input = new PictogramaConCategoriasInput();
     input.setNombre("Hola");
     input.setImagen("url");
     input.setTipo("verbo");
     input.setCategorias(List.of(categoriaId));

     Usuario usuario = new Usuario();
     usuario.setId(usuarioId);

     Categoria categoria = new Categoria();
     categoria.setNombre("Saludo");

     Pictograma pictogramaGuardado = new Pictograma();
     pictogramaGuardado.setNombre("Hola");
     pictogramaGuardado.setTipo("verbo");
     pictogramaGuardado.setImagen("url");
     pictogramaGuardado.setUsuario(usuario);

     when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
     when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
     when(pictogramaRepository.save(any(Pictograma.class))).thenReturn(pictogramaGuardado);
     when(pictogramaCategoriaRepository.buscarCategoriasDePictogramaPorUsuario(null, usuarioId))
         .thenReturn(List.of(categoria));

     PictogramaConCategorias resultado = pictogramaService.crearPictograma(input, usuarioId);

     assertEquals("Hola", resultado.getNombre());
     assertEquals("verbo", resultado.getTipo());
     assertEquals("url", resultado.getImagen());
     assertEquals(1, resultado.getCategorias().size());
     assertEquals("Saludo", resultado.getCategorias().get(0).getNombre());

     verify(pictogramaRepository).save(any(Pictograma.class));
     verify(pictogramaCategoriaRepository).save(any(PictogramaCategoria.class));
 }

 @Test
 void crearDesdeInputDTO_sinUsuario_creaPictogramaGeneral() {
     Long categoriaId = 200L;

     PictogramaConCategoriasInput input = new PictogramaConCategoriasInput();
     input.setNombre("Comer");
     input.setImagen("url2");
     input.setTipo("verbo");
     input.setCategorias(List.of(categoriaId));

     Categoria categoria = new Categoria();
     categoria.setNombre("Acciones");

     Pictograma pictogramaGuardado = new Pictograma();
     pictogramaGuardado.setNombre("Comer");
     pictogramaGuardado.setTipo("verbo");
     pictogramaGuardado.setImagen("url2");
     pictogramaGuardado.setUsuario(null); // general

     when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
     when(pictogramaRepository.save(any(Pictograma.class))).thenReturn(pictogramaGuardado);
     when(pictogramaCategoriaRepository.buscarCategoriasDePictogramaPorUsuario(any(), any()))
         .thenReturn(List.of(categoria));

     PictogramaConCategorias resultado = pictogramaService.crearPictograma(input, null);

     assertEquals("Comer", resultado.getNombre());
     assertEquals("verbo", resultado.getTipo());
     assertEquals("url2", resultado.getImagen());
     assertEquals(1, resultado.getCategorias().size());
     assertEquals("Acciones", resultado.getCategorias().get(0).getNombre());

     verify(pictogramaRepository).save(any(Pictograma.class));
     verify(pictogramaCategoriaRepository).save(any(PictogramaCategoria.class));
 }

 @Test
 void crearDesdeInputDTO_categoriaNoExiste_lanzaExcepcion() {
     Long usuarioId = 2L;
     Long categoriaIdInexistente = 999L;

     PictogramaConCategoriasInput input = new PictogramaConCategoriasInput();
     input.setNombre("Dormir");
     input.setImagen("zzz.png");
     input.setTipo("verbo");
     input.setCategorias(List.of(categoriaIdInexistente));

     Usuario usuario = new Usuario();
     usuario.setId(usuarioId);

     when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
     when(categoriaRepository.findById(categoriaIdInexistente)).thenReturn(Optional.empty());

     RuntimeException exception = assertThrows(RuntimeException.class, () ->
         pictogramaService.crearPictograma(input, usuarioId)
     );

     assertEquals("Categoría no encontrada con ID: " + categoriaIdInexistente, exception.getMessage());
 }

 @Test
 void crearDesdeInputDTO_sinCategorias_creaPictogramaSinRelaciones() {
     Long usuarioId = 3L;

     PictogramaConCategoriasInput input = new PictogramaConCategoriasInput();
     input.setNombre("Leer");
     input.setImagen("libro.png");
     input.setTipo("verbo");
     input.setCategorias(null); // sin categorías

     Usuario usuario = new Usuario();
     usuario.setId(usuarioId);

     Pictograma pictogramaGuardado = new Pictograma();
     pictogramaGuardado.setNombre("Leer");
     pictogramaGuardado.setTipo("verbo");
     pictogramaGuardado.setImagen("libro.png");
     pictogramaGuardado.setUsuario(usuario);

     when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
     when(pictogramaRepository.save(any(Pictograma.class))).thenReturn(pictogramaGuardado);
     when(pictogramaCategoriaRepository.buscarCategoriasDePictogramaPorUsuario(any(), any()))
         .thenReturn(List.of()); // <- ✅ cambio aquí

     PictogramaConCategorias resultado = pictogramaService.crearPictograma(input, usuarioId);

     assertEquals("Leer", resultado.getNombre());
     assertEquals("verbo", resultado.getTipo());
     assertEquals("libro.png", resultado.getImagen());
     assertEquals(0, resultado.getCategorias().size());

     verify(pictogramaRepository).save(any(Pictograma.class));
     verify(pictogramaCategoriaRepository, org.mockito.Mockito.never()).save(any());
 }
 
 @Test
 void eliminarPictograma_conUsuarioValidoYPropietario_eliminaCorrectamente() {
     Long pictogramaId = 1L;
     Long usuarioId = 10L;

     Usuario usuario = new Usuario();
     usuario.setId(usuarioId);

     Pictograma pictograma = new Pictograma();
     pictograma.setUsuario(usuario); // propietario

     when(pictogramaRepository.findById(pictogramaId)).thenReturn(Optional.of(pictograma));

     pictogramaService.eliminarPictograma(pictogramaId,usuarioId);

     verify(pictogramaRepository).delete(pictograma);
 }

 @Test
 void eliminarPictograma_pictogramaNoExiste_lanzaExcepcionPersonalizada() {
     Long pictogramaId = 2L;
     Long usuarioId = 10L;

     when(pictogramaRepository.findById(pictogramaId)).thenReturn(Optional.empty());

     assertThrows(PictogramaNoEncontradoException.class, () ->
         pictogramaService.eliminarPictograma(pictogramaId, usuarioId)
     );
 }


 @Test
 void eliminarPictograma_usuarioNoEsPropietario_lanzaExcepcionPersonalizada() {
     Long pictogramaId = 3L;
     Long usuarioId = 10L;
     Long otroUsuarioId = 99L;

     Usuario otroUsuario = new Usuario();
     otroUsuario.setId(otroUsuarioId);

     Pictograma pictograma = new Pictograma();
     pictograma.setUsuario(otroUsuario); // no es el propietario

     when(pictogramaRepository.findById(pictogramaId)).thenReturn(Optional.of(pictograma));

     assertThrows(AccesoPictogramaDenegadoException.class, () ->
         pictogramaService.eliminarPictograma(pictogramaId, usuarioId)
     );
 }

 
 @Test
 void obtenerPictogramaConCategorias_conIdYUsuarioValido_devuelveDTO() {
     Long pictogramaId = 1L;
     Long usuarioId = 1L;

     Pictograma pictograma = new Pictograma();
     pictograma.setNombre("Dormir");
     pictograma.setImagen("zzz.png");
     pictograma.setTipo("verbo");

     when(pictogramaRepository.findById(pictogramaId)).thenReturn(Optional.of(pictograma));
     when(pictogramaCategoriaRepository.buscarCategoriasDePictogramaPorUsuario(
    		    eq(null), eq(1L))
    		).thenReturn(List.of());


     PictogramaConCategorias resultado = pictogramaService.obtenerPictogramaConCategorias(pictogramaId, usuarioId);

     assertEquals("Dormir", resultado.getNombre());
     assertEquals("zzz.png", resultado.getImagen());
     assertEquals("verbo", resultado.getTipo());
     assertTrue(resultado.getCategorias().isEmpty());

     verify(pictogramaRepository).findById(pictogramaId);
 }

 @Test
 void obtenerPictogramaConCategorias_conIdInexistente_lanzaExcepcionPersonalizada() {
     Long pictogramaId = 999L;
     Long usuarioId = 1L;

     when(pictogramaRepository.findById(pictogramaId)).thenReturn(Optional.empty());

     PictogramaNoEncontradoException ex = assertThrows(PictogramaNoEncontradoException.class, () ->
         pictogramaService.obtenerPictogramaConCategorias(pictogramaId, usuarioId)
     );

     assertThrows(PictogramaNoEncontradoException.class, () ->
     pictogramaService.obtenerPictogramaConCategorias(pictogramaId, usuarioId)
 );

 }


 @Test
 void obtenerPictogramaConCategorias_conUsuarioNull_devuelveDTOGeneral() {
     Long pictogramaId = 1L;

     Pictograma pictograma = new Pictograma();
     pictograma.setNombre("Comer");
     pictograma.setImagen("comer.png");
     pictograma.setTipo("verbo");

     when(pictogramaRepository.findById(pictogramaId)).thenReturn(Optional.of(pictograma));
     when(pictogramaCategoriaRepository.buscarCategoriasDePictogramaPorUsuario(null, null))
         .thenReturn(List.of());

     PictogramaConCategorias resultado = pictogramaService.obtenerPictogramaConCategorias(pictogramaId, null);

     assertEquals("Comer", resultado.getNombre());
     assertEquals("comer.png", resultado.getImagen());
     assertEquals("verbo", resultado.getTipo());
     assertTrue(resultado.getCategorias().isEmpty());
 }

}



