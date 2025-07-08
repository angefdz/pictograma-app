package com.example.tfg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.app.model.Configuracion;
import com.example.app.model.Usuario;
import com.example.app.repository.ConfiguracionRepository;
import com.example.app.repository.PictogramaCategoriaRepository;
import com.example.app.repository.UsuarioRepository;
import com.example.app.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ConfiguracionRepository configuracionRepository;

    @Mock
    private PictogramaCategoriaRepository pictogramaCategoriaRepository;

    @Test
    void registrarUsuario_UsuarioNuevo_RegistraCorrectamente() {
        Usuario nuevo = new Usuario();
        nuevo.setEmail("nuevo@correo.com");
        nuevo.setContrasena("1234");

        when(usuarioRepository.buscarPorEmail(nuevo.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        boolean resultado = authService.registrarUsuario(nuevo);

        assertTrue(resultado);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(configuracionRepository).save(any(Configuracion.class));
        verify(pictogramaCategoriaRepository).findAllGenerales();
    }

    @Test
    void registrarUsuario_CorreoYaExiste_DevuelveFalse() {
        Usuario existente = new Usuario();
        existente.setEmail("ya@existe.com");

        when(usuarioRepository.buscarPorEmail("ya@existe.com")).thenReturn(Optional.of(existente));

        boolean resultado = authService.registrarUsuario(existente);

        assertFalse(resultado);
        verify(usuarioRepository, never()).save(any());
    }
    
    @Test
    void autenticarUsuario_ValidoYManual_DevuelveTrue() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@correo.com");
        usuario.setContrasena("hashed");

        when(usuarioRepository.buscarPorEmail("test@correo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hashed")).thenReturn(true);

        boolean resultado = authService.autenticarUsuario("test@correo.com", "1234");

        assertTrue(resultado);
    }

    @Test
    void autenticarUsuario_MetodoGoogle_DevuelveFalse() {
        Usuario usuario = new Usuario();

        when(usuarioRepository.buscarPorEmail("google@correo.com")).thenReturn(Optional.of(usuario));

        boolean resultado = authService.autenticarUsuario("google@correo.com", "1234");

        assertFalse(resultado);
    }

    @Test
    void cambiarContrasena_Valida_CambiaCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setEmail("cambiar@correo.com");
        usuario.setContrasena("oldHash");

        when(usuarioRepository.buscarPorEmail("cambiar@correo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("actual123", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("nueva123")).thenReturn("newHash");

        boolean resultado = authService.cambiarContrasena("cambiar@correo.com", "actual123", "nueva123");

        assertTrue(resultado);
        assertEquals("newHash", usuario.getContrasena());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarContrasena_ContrasenaActualIncorrecta_DevuelveFalse() {
        Usuario usuario = new Usuario();
        usuario.setContrasena("oldHash");

        when(usuarioRepository.buscarPorEmail("fail@correo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "oldHash")).thenReturn(false);

        boolean resultado = authService.cambiarContrasena("fail@correo.com", "incorrecta", "nueva");

        assertFalse(resultado);
        verify(usuarioRepository, never()).save(any());
    }

}