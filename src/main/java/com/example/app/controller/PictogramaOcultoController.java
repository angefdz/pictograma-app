package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.PictogramaConCategorias;
import com.example.app.model.Usuario;
import com.example.app.service.PictogramaOcultoService;
import com.example.app.service.UsuarioService;

@RestController
@RequestMapping("/pictogramas-ocultos")
public class PictogramaOcultoController {

    @Autowired
    private PictogramaOcultoService pictogramaOcultoService;
    
    @Autowired
    private UsuarioService usuarioService;

    
    private String getCorreoAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Usuario usuario) {
            return usuario.getEmail();
        }

        return principal.toString();
    }
    
    @PostMapping("/ocultar")
    public ResponseEntity<String> ocultarPictograma(@RequestParam Long pictogramaId) {
        String correo = getCorreoAutenticado();
        Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);

        boolean resultado = pictogramaOcultoService.ocultarPorIds(pictogramaId, usuarioId);
        if (!resultado) {
            return ResponseEntity.badRequest().body("Pictograma ya estaba oculto o no existe");
        }
        return ResponseEntity.ok("Pictograma ocultado correctamente");
    }

    @DeleteMapping("/desocultar")
    public ResponseEntity<String> desocultarPictograma(@RequestParam Long pictogramaId) {
        String correo = getCorreoAutenticado();
        Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);

        boolean resultado = pictogramaOcultoService.desocultarPorIds(pictogramaId, usuarioId);
        if (!resultado) {
            return ResponseEntity.badRequest().body("Pictograma no estaba oculto o no existe");
        }
        return ResponseEntity.ok("Pictograma desocultado correctamente");
    }

    
    @GetMapping("/es-oculto")
    public ResponseEntity<Boolean> estaOculto(@RequestParam Long pictogramaId, @RequestParam Long usuarioId) {
        boolean resultado = pictogramaOcultoService.estaOculto(pictogramaId, usuarioId);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<PictogramaConCategorias>> getPictogramasOcultos() {
        try {
            String correo = getCorreoAutenticado();  // Método que obtienes del token
            Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);
            System.out.println("El id del usuario es: "+ usuarioId);
            List<PictogramaConCategorias> ocultos = pictogramaOcultoService.obtenerPictogramasOcultos(usuarioId);
            if (ocultos.isEmpty()) {
            	System.out.println("Esta puta mierda no va");
            }else {
            	System.out.println("Si que va");
            }
            return ResponseEntity.ok(ocultos);
        } catch (RuntimeException e) {
            System.out.println("Error al obtener ID del usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}
