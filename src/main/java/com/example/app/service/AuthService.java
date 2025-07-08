package com.example.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <--- IMPORTE IMPORTANTE

import com.example.app.model.Configuracion;     // <--- IMPORTE IMPORTANTE
import com.example.app.model.PictogramaCategoria;
import com.example.app.model.TipoVoz;          // <--- IMPORTE IMPORTANTE
import com.example.app.model.Usuario;
import com.example.app.repository.ConfiguracionRepository; // <--- IMPORTE IMPORTANTE
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.UsuarioRepository;

@Service
public class AuthService {

	
	@Autowired
	private PictogramaCategoriaRepository pictogramaCategoriaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfiguracionRepository configuracionRepository; 
    
    private boolean esContrasenaSegura(String contrasena) {
        if (contrasena == null) return false;

       return contrasena.length() >= 8 &&
               contrasena.matches(".*[a-z].*") &&     // minúscula
               contrasena.matches(".*[A-Z].*") &&     // mayúscula
             contrasena.matches(".*\\d.*") &&       // número
               contrasena.matches(".*[^a-zA-Z0-9].*"); // símbolo
    }
    @Transactional 
    public boolean registrarUsuario(Usuario usuario) {
        if (usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            return false; 
        }
        if (!esContrasenaSegura(usuario.getContrasena())) {
            throw new IllegalArgumentException("La contraseña no es segura. Debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial.");
        }

        String contrasenaEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(contrasenaEncriptada);

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        Configuracion configuracionDefault = new Configuracion();
        configuracionDefault.setUsuario(nuevoUsuario); 
        configuracionDefault.setBotonesPorPantalla(9); 
        configuracionDefault.setTipoVoz(TipoVoz.femenina); 

        configuracionRepository.save(configuracionDefault);

        nuevoUsuario.setConfiguracion(configuracionDefault);

        inicializarRelacionesParaUsuarioNuevo(nuevoUsuario);

        return true; 
    }

    public boolean autenticarUsuario(String email, String contrasenaPlano) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(email);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();

        if (!"manual".equalsIgnoreCase(usuario.getMetodoAutenticacion())) {
            return false; 
        }
        return passwordEncoder.matches(contrasenaPlano, usuario.getContrasena());
    }

    @Transactional 
    public Usuario registrarUsuarioGoogle(Usuario usuario) {
        if (usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalStateException("El usuario con ese correo ya existe");
        }

        usuario.setMetodoAutenticacion("google");

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        Configuracion configuracionDefault = new Configuracion();
        configuracionDefault.setUsuario(nuevoUsuario);
        configuracionDefault.setBotonesPorPantalla(9);
        configuracionDefault.setMostrarPorCategoria(false);
        configuracionDefault.setTipoVoz(TipoVoz.femenina);

        configuracionRepository.save(configuracionDefault);

        return nuevoUsuario;
    }


    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }
    
    @Transactional
    private void inicializarRelacionesParaUsuarioNuevo(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("Usuario no válido para inicializar relaciones.");
        }

        List<PictogramaCategoria> relacionesGenerales = pictogramaCategoriaRepository.findAllGenerales();

        List<PictogramaCategoria> nuevasRelaciones = new ArrayList<>(relacionesGenerales.size());

        for (PictogramaCategoria general : relacionesGenerales) {
            PictogramaCategoria relacion = new PictogramaCategoria();
            relacion.setCategoria(general.getCategoria());
            relacion.setPictograma(general.getPictograma());
            relacion.setUsuario(usuario);
            nuevasRelaciones.add(relacion);
        }

        pictogramaCategoriaRepository.saveAll(nuevasRelaciones);
    }

    
    @Transactional
    public boolean cambiarContrasena(String email, String contrasenaActual, String nuevaContrasena) {
        Optional<Usuario> optional = usuarioRepository.buscarPorEmail(email);
        if (optional.isEmpty()) return false;

        Usuario usuario = optional.get();

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            return false; 
        }

        String contrasenaEncriptada = passwordEncoder.encode(nuevaContrasena);
        usuario.setContrasena(contrasenaEncriptada);
        usuarioRepository.save(usuario);

        return true;
    }

     
    
}