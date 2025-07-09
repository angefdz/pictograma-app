package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.UsuarioSimple;
import com.example.app.model.Usuario;
import com.example.app.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	
	 private String getCorreoAutenticado() {
	        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	        if (principal instanceof Usuario usuario) {
	            return usuario.getEmail();
	        }

	        return principal.toString();
	    }
    @Autowired
    private UsuarioService usuarioService;

    
    @PutMapping("/yo")
    public ResponseEntity<?> actualizarUsuario(@RequestBody UsuarioSimple datos) {
        String correo = getCorreoAutenticado();

        return usuarioService.editarUsuario(correo, datos)
                .map(usuario -> ResponseEntity.ok("Usuario actualizado"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping()
    public ResponseEntity<List<Usuario>> getUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/yo")
    public ResponseEntity<UsuarioSimple> getUsuarioActual() {
        try {
        	
            String correo = getCorreoAutenticado(); 
            Long usuarioId = usuarioService.obtenerId(correo);

            return usuarioService.obtenerPorId(usuarioId)
                .map(usuario -> {
                    UsuarioSimple dto = new UsuarioSimple();
                    dto.setId(usuario.getId());
                    dto.setCorreo(usuario.getEmail());
                    dto.setNombre(usuario.getNombre());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @DeleteMapping("/me")
    public ResponseEntity<Object> eliminarMiCuenta() {
        String correo = getCorreoAutenticado();
        boolean eliminado = usuarioService.eliminarUsuario(correo);

        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado.");
        }
    }

}