package com.example.app.controller;

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

import com.example.app.dto.ConfiguracionSimple; // ¡Importa tu nuevo DTO!
import com.example.app.model.Configuracion; // Necesario si mantienes los métodos POST/DELETE que operan con la entidad completa
import com.example.app.model.TipoVoz;
import com.example.app.model.Usuario;
import com.example.app.service.ConfiguracionService;
import com.example.app.service.UsuarioService;

@RestController
@RequestMapping("/configuraciones")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;

    @Autowired
    private UsuarioService usuarioService;
    
    private String getCorreoAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Usuario usuario) {
            return usuario.getEmail();
        }

        return principal.toString();
    }
    
    @GetMapping("/usuario/yo")
    public ResponseEntity<ConfiguracionSimple> obtenerConfiguracionDelUsuarioAutenticado() {
        try {
            String correo = getCorreoAutenticado();
            Long usuarioId = usuarioService.obtenerId(correo);

            Optional<ConfiguracionSimple> configuracionSimple = configuracionService.getConfiguracionSimpleByUsuarioId(usuarioId);

            if (configuracionSimple.isPresent()) {
                return ResponseEntity.ok(configuracionSimple.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping
    public ResponseEntity<Configuracion> crearOActualizar(@RequestBody Configuracion configuracion) {
        Configuracion guardada = configuracionService.guardarConfiguracion(configuracion);
        return ResponseEntity.ok(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        configuracionService.eliminarConfiguracion(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Configuracion> actualizar(@PathVariable Integer id, @RequestBody Configuracion configuracionActualizada) {
        try {
            String correo = getCorreoAutenticado();
            Long usuarioId = usuarioService.obtenerId(correo);

            configuracionActualizada.setId(id);
            configuracionActualizada.setUsuario(
                usuarioService.obtenerPorId(usuarioId).orElseThrow()
            );

            Configuracion actualizada = configuracionService.guardarConfiguracion(configuracionActualizada);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    
    @GetMapping("/tipos-voz")
    public ResponseEntity<TipoVoz[]> obtenerTiposVoz() {
        return ResponseEntity.ok(TipoVoz.values());
    }


}