package com.example.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <--- IMPORTE IMPORTANTE

import com.example.app.model.Categoria;
import com.example.app.model.Configuracion;     // <--- IMPORTE IMPORTANTE
import com.example.app.model.Pictograma;
import com.example.app.model.PictogramaCategoria;
import com.example.app.model.TipoVoz;          // <--- IMPORTE IMPORTANTE
import com.example.app.model.Usuario;
import com.example.app.repository.CategoriaRepository;
import com.example.app.repository.ConfiguracionRepository; // <--- IMPORTE IMPORTANTE
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.PictogramaRepository;
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
    
    @Transactional 
    public boolean registrarUsuario(Usuario usuario) {
        if (usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            System.out.println("Ya existe");
            return false; 
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
        System.out.println(passwordEncoder.matches(contrasenaPlano, usuario.getContrasena()));
        return passwordEncoder.matches(contrasenaPlano, usuario.getContrasena());
    }

    @Transactional 
    public Usuario registrarUsuarioGoogle(Usuario usuario) {
        // Validación opcional (puedes quitarla si ya haces el control antes)
        if (usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalStateException("El usuario con ese correo ya existe");
        }

        usuario.setMetodoAutenticacion("google");

        // Guardar el nuevo usuario
        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        // Crear configuración por defecto
        Configuracion configuracionDefault = new Configuracion();
        configuracionDefault.setUsuario(nuevoUsuario);
        configuracionDefault.setBotonesPorPantalla(9);
        configuracionDefault.setMostrarPorCategoria(false);
        configuracionDefault.setTipoVoz(TipoVoz.femenina);

        configuracionRepository.save(configuracionDefault);

        // Asociar la configuración al usuario
        nuevoUsuario.setConfiguracion(configuracionDefault);

        return nuevoUsuario;
    }


    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }
    
    private void inicializarRelacionesParaUsuarioNuevo(Usuario usuario) {
    	List<PictogramaCategoria> relacionesGenerales = pictogramaCategoriaRepository.findAllGenerales();

    	for (PictogramaCategoria general : relacionesGenerales) {
    	    PictogramaCategoria relacion = new PictogramaCategoria();
    	    relacion.setCategoria(general.getCategoria());
    	    relacion.setPictograma(general.getPictograma());
    	    relacion.setUsuario(usuario);
    	    pictogramaCategoriaRepository.save(relacion);
    	}
    }
    
    @Transactional
    public boolean cambiarContrasena(String email, String contrasenaActual, String nuevaContrasena) {
        Optional<Usuario> optional = usuarioRepository.buscarPorEmail(email);
        if (optional.isEmpty()) return false;

        Usuario usuario = optional.get();

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            return false; // la contraseña actual no coincide
        }

        String contrasenaEncriptada = passwordEncoder.encode(nuevaContrasena);
        usuario.setContrasena(contrasenaEncriptada);
        usuarioRepository.save(usuario);

        return true;
    }

     
    
}