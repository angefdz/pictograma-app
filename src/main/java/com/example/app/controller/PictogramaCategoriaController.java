package com.example.app.controller;

import com.example.app.model.Usuario;
import com.example.app.service.PictogramaCategoriaService;
import com.example.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pictograma-categoria")
public class PictogramaCategoriaController {

    @Autowired
    private PictogramaCategoriaService pictogramaCategoriaService;

    @Autowired
    private UsuarioService usuarioService;

    private String getCorreoAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Usuario usuario) {
            return usuario.getEmail();
        }

        return principal.toString();
    }
    
    


    
}
