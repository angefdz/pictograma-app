package com.example.app.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.app.dto.CategoriaConPictogramas;
import com.example.app.dto.CategoriaConPictogramasInput;
import com.example.app.dto.CategoriaSimple;
import com.example.app.dto.CategoriaUsuarioInput;
import com.example.app.model.Categoria;
import com.example.app.model.Usuario;
import com.example.app.service.CategoriaService;
import com.example.app.service.UsuarioService;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    private String getCorreoAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Usuario usuario) {
        	System.out.println("El correo de los cojones es: "+ usuario.getEmail());
            return usuario.getEmail();
        }
        
        System.out.println("No se que cojones tiene que mandar aquí: "+ principal.toString());
        return principal.toString();
    }
    
    @PostMapping("/general")
    public ResponseEntity<CategoriaConPictogramas> crearCategoriaGeneral(@RequestBody CategoriaConPictogramasInput input) {
        System.out.println("Entra");

        if (input == null || input.getNombre() == null || input.getNombre().isBlank()) {
        	System.out.println("Tus muertos");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("intentándolo");
        CategoriaConPictogramas nueva = categoriaService.crearDesdeInput(input); // ya pone usuario = null
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }


    @PostMapping
    public ResponseEntity<CategoriaConPictogramas> createCategoria(@RequestBody CategoriaConPictogramasInput input) {
    	if (input == null || input.getNombre() == null || input.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
    	CategoriaConPictogramas nueva = categoriaService.crearDesdeInput(input);
        return new ResponseEntity<>(nueva, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaConPictogramas>> getCategoriasDefault() {
        List<CategoriaConPictogramas> categorias = categoriaService.obtenerCategoriasGeneralesConPictogramas();
        if (categorias.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @GetMapping("/usuarios/{usuario_id}")
    public ResponseEntity<List<CategoriaConPictogramas>> getCategoriasPersonalizados(@PathVariable Long usuario_id) {
        String correo = getCorreoAutenticado();
        Long idAutenticado = usuarioService.obtenerIdPorCorreo(correo);

        if (!idAutenticado.equals(usuario_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }

        List<CategoriaConPictogramas> categorias = categoriaService.obtenerCategoriasPersonalizadasConPictogramas(usuario_id);
        if (categorias.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaConPictogramas> getCategoriaById(@PathVariable Long id) {
        Optional<CategoriaConPictogramas> categoria = categoriaService.obtenerCategoriaConPictogramasOpt(id, null);
        return categoria.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaConPictogramas> updateCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaConPictogramasInput input
    ) {
    	if (input == null || input.getNombre() == null || input.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        String correo = getCorreoAutenticado();
        Long idAutenticado = usuarioService.obtenerIdPorCorreo(correo);

        Optional<Categoria> categoriaOpt = categoriaService.obtenerCategoriaPorId(id);
        if (categoriaOpt.isEmpty()) return ResponseEntity.notFound().build();

        Categoria categoria = categoriaOpt.get();

        if (categoria.getUsuario() == null) {
            // ✅ Categoría general → solo actualizar relaciones
            categoriaService.actualizarPictogramasRelacionados(id, input.getPictogramas(), idAutenticado);
            CategoriaConPictogramas resultado = categoriaService.obtenerCategoriaConPictogramas(id, idAutenticado);
            return ResponseEntity.ok(resultado);
        }

        if (categoria.getUsuario().getId().equals(idAutenticado)) {
            // ✅ Categoría personalizada → actualizar todo
            Optional<CategoriaConPictogramas> actualizada = categoriaService.actualizarCategoria(id, input);
            return actualizada.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        // ❌ Categoría de otro usuario → denegar acceso
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        String correo = getCorreoAutenticado();
        Long idAutenticado = usuarioService.obtenerIdPorCorreo(correo);

        try {
            categoriaService.eliminarCategoria(id, idAutenticado);
            return ResponseEntity.noContent().build();
        } catch (CategoriaService.CategoriaNoEncontradaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (CategoriaService.AccesoProhibidoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }




    @PostMapping("/usuario")
    public ResponseEntity<CategoriaConPictogramas> crearCategoriaUsuario(@RequestBody CategoriaUsuarioInput input) {
        String correo = getCorreoAutenticado();
        Long idAutenticado = usuarioService.obtenerIdPorCorreo(correo);

        if (!idAutenticado.equals(input.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CategoriaConPictogramas nueva = categoriaService.crearCategoriaDeUsuario(input);
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    @PostMapping("/por-ids")
    public ResponseEntity<List<CategoriaSimple>> obtenerCategoriasPorIds(@RequestBody List<Long> ids) {
        List<CategoriaSimple> categorias = categoriaService.obtenerCategoriasPorIds(ids);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/visibles")
    public ResponseEntity<List<CategoriaSimple>> getCategoriasVisibles() {
        System.out.println("ENTRO EN EL ENDPOINT CORRECTO");
        try {
            String correo = getCorreoAutenticado();
            Long idAutenticado = usuarioService.obtenerIdPorCorreo(correo);
            System.out.println("El usuario id es: " + idAutenticado);

            List<CategoriaSimple> categorias = categoriaService.obtenerCategoriasVisiblesParaUsuario(idAutenticado);
            if (categorias.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(categorias);
        } catch (RuntimeException e) {
            System.out.println("Error al obtener ID del usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/pictograma/{pictogramaId}")
    public ResponseEntity<List<CategoriaSimple>> getCategoriasDePictograma(@PathVariable Long pictogramaId) {
        try {
            String correo = getCorreoAutenticado();
            Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);
            List<CategoriaSimple> categorias = categoriaService.obtenerCategoriasDePictogramaParaUsuario(pictogramaId, usuarioId);

            if (categorias.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(categorias);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/con-pictogramas")
    public ResponseEntity<List<CategoriaConPictogramas>> obtenerCategoriasConPictogramas() {
    	System.out.println(">>> Entrando en obtenerCategoriasConPictogramas");
        try {
            System.out.println("ESTOY ENSEÑANDO LAS CATEGORÍAS");
            String correo = getCorreoAutenticado();
            Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);
            System.out.println("Mi usuario es:" +  usuarioId);
            List<CategoriaConPictogramas> resultado = categoriaService.obtenerCategoriasConPictogramasVisibles(usuarioId);

            if (resultado.isEmpty()) {
                System.out.println("VACÍO");
                return ResponseEntity.noContent().build();
            }


            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



}
