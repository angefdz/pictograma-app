package com.example.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dto.UsuarioSimple;
import com.example.app.model.Categoria;
import com.example.app.model.Pictograma;
import com.example.app.model.PictogramaCategoria;
// Eliminadas: Configuracion, TipoVoz, ConfiguracionRepository,
// ya que 'crearUsuario' con la lógica de configuración no se usa aquí para el registro inicial.
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaRepository;
import com.example.app.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired PictogramaRepository pictogramaRepository;
    
    @Autowired PictogramaCategoriaRepository pictogramaCategoriaRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.buscarPorId(id);}
    
    
    public boolean eliminarUsuarioPorCorreo(String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(correo);
        if (usuarioOpt.isEmpty()) {
        	System.out.println("Estoy vacío");
            return false;
        }
        System.out.println("Voy a eliminar a : "+ correo);
        usuarioRepository.deleteById(usuarioOpt.get().getId());
        return true;
    }


    @Transactional
    public boolean eliminarUsuario(Long id) {
        return usuarioRepository.buscarPorId(id).map(u -> {
            usuarioRepository.delete(u);
            return true;
        }).orElse(false);
    }
    
    public Long obtenerIdPorCorreo(String correo) {
        return usuarioRepository.buscarPorEmail(correo.trim())
            .map(Usuario::getId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + correo));
    }
    
    public Optional<Usuario> editarUsuarioPorCorreo(String correo, UsuarioSimple datos) {
        return usuarioRepository.buscarPorEmail(correo).map(usuario -> {
            
            if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
                usuario.setNombre(datos.getNombre());
            }

            return usuarioRepository.save(usuario);
        });
    }

    @Transactional
    public Optional<Usuario> cambiarContrasena(String correo, String nuevaContrasena) {
        return usuarioRepository.buscarPorEmail(correo).map(u -> {
            u.setContrasena(passwordEncoder.encode(nuevaContrasena)); // ✅ Igual que al registrarse
            return usuarioRepository.save(u);
        });
    }
  

    




}