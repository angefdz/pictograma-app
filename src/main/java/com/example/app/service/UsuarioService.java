package com.example.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.dto.UsuarioSimple;
import com.example.app.model.Usuario;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired PictogramaRepository pictogramaRepository;
    
    @Autowired PictogramaCategoriaRepository pictogramaCategoriaRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.buscarPorId(id);}
    
    
    public boolean eliminarUsuario(String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(correo);
        if (usuarioOpt.isEmpty()) {
        	System.out.println("Estoy vacío");
            return false;
        }
        System.out.println("Voy a eliminar a : "+ correo);
        usuarioRepository.deleteById(usuarioOpt.get().getId());
        return true;
    }

    public Long obtenerId(String correo) {
        return usuarioRepository.buscarPorEmail(correo.trim())
            .map(Usuario::getId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + correo));
    }
    
    public Optional<Usuario> editarUsuario(String correo, UsuarioSimple datos) {
        return usuarioRepository.buscarPorEmail(correo).map(usuario -> {
            
            if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
                usuario.setNombre(datos.getNombre());
            }

            return usuarioRepository.save(usuario);
        });
    }


}