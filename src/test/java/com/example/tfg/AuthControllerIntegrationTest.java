package com.example.tfg;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.app.TfgApplication;
import com.example.app.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = TfgApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registroYLoginCorrectos() throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Test Nombre");
        nuevoUsuario.setEmail("test@correo.com");
        nuevoUsuario.setContrasena("UnaContrasena123!");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"));
    }

    @Test
    void errorAlRegistrarUsuarioConCorreoExistente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ángela");
        usuario.setEmail("correo@ejemplo.com");
        usuario.setContrasena("ClaveSegura123!");

        // Registro inicial
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        // Segundo intento con el mismo correo
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El correo ya está registrado"));
    }

    @Test
    void errorAlRegistrarUsuarioSinCamposValidos() throws Exception {
        Usuario usuarioIncompleto = new Usuario();
        usuarioIncompleto.setEmail("incompleto@ejemplo.com");
        usuarioIncompleto.setContrasena("ClaveSegura123!");
        // ❌ Falta el nombre

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioIncompleto)))
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest());
// Ajusta si tu mensaje es distinto
    }
    
    @Test
    void loginConCredencialesValidasDevuelveToken() throws Exception {
        // Primero registramos al usuario
        Usuario usuario = new Usuario();
        usuario.setNombre("Login Test");
        usuario.setEmail("login@ejemplo.com");
        usuario.setContrasena("MiClaveSegura1!");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        // Intentamos login con las mismas credenciales
        Usuario login = new Usuario();
        login.setEmail("login@ejemplo.com");
        login.setContrasena("MiClaveSegura1!");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.usuarioId").exists());
    }

    @Test
    void loginConContrasenaIncorrectaDevuelveError() throws Exception {
        // Registro previo
        Usuario usuario = new Usuario();
        usuario.setNombre("Fallido");
        usuario.setEmail("fallido@ejemplo.com");
        usuario.setContrasena("Correcta123!");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        // Intentamos login con contraseña incorrecta
        Usuario loginFallido = new Usuario();
        loginFallido.setEmail("fallido@ejemplo.com");
        loginFallido.setContrasena("Incorrecta999");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginFallido)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    void loginConCamposVaciosDevuelveError() throws Exception {
        Usuario loginIncompleto = new Usuario();
        // No se establece email ni contraseña

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginIncompleto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void loginConCorreoNoRegistradoDevuelveError() throws Exception {
        Usuario login = new Usuario();
        login.setEmail("inexistente@correo.com");
        login.setContrasena("CualquierClave123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }
    
    @Test
    void cambiarContrasenaCorrectamente() throws Exception {
        Usuario usuario = new Usuario("Cambio", "cambio@ejemplo.com", "ViejaContrasena1!", "manual");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "cambio@ejemplo.com",
                "contrasena", "ViejaContrasena1!"
        ));

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        String cambioBody = objectMapper.writeValueAsString(Map.of(
                "contrasenaActual", "ViejaContrasena1!",
                "nuevaContrasena", "NuevaContrasena2@"
        ));

        mockMvc.perform(put("/auth/cambiar-contrasena")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(cambioBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Contraseña actualizada correctamente"));
    }

    @Test
    void cambiarContrasenaConActualIncorrectaDevuelve401() throws Exception {
        Usuario usuario = new Usuario("ErrorPass", "errorpass@ejemplo.com", "PassCorrecta1!", "manual");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "errorpass@ejemplo.com",
                "contrasena", "PassCorrecta1!"
        ));

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        String cambioBody = objectMapper.writeValueAsString(Map.of(
                "contrasenaActual", "Incorrecta!",
                "nuevaContrasena", "Nueva123!"
        ));

        mockMvc.perform(put("/auth/cambiar-contrasena")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(cambioBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("La contraseña actual no es válida"));
    }

    @Test
    void cambiarContrasenaFaltanCamposDevuelve400() throws Exception {
        Usuario usuario = new Usuario("FaltanCampos", "faltan@ejemplo.com", "Clave123@", "manual");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "faltan@ejemplo.com",
                "contrasena", "Clave123@"
        ));

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Falta campo "nuevaContrasena"
        String cambioBody = objectMapper.writeValueAsString(Map.of(
                "contrasenaActual", "Clave123@"
        ));

        mockMvc.perform(put("/auth/cambiar-contrasena")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(cambioBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Faltan datos obligatorios"));
    }

    @Test
    void cambiarContrasenaInseguraDevuelve400() throws Exception {
        Usuario usuario = new Usuario("Insegura", "insegura@ejemplo.com", "Segura123!", "manual");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "insegura@ejemplo.com",
                "contrasena", "Segura123!"
        ));

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Contraseña muy débil
        String cambioBody = objectMapper.writeValueAsString(Map.of(
                "contrasenaActual", "Segura123!",
                "nuevaContrasena", "abc"
        ));

        mockMvc.perform(put("/auth/cambiar-contrasena")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(cambioBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La nueva contraseña no es segura"));
    }
    
    @Test
    void cambiarContrasenaSinNingunCampoDevuelve400() throws Exception {
        Usuario usuario = new Usuario("CamposVacios", "vacios@ejemplo.com", "Valida123!", "manual");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "vacios@ejemplo.com",
                "contrasena", "Valida123!"
        ));

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Enviar un JSON vacío
        mockMvc.perform(put("/auth/cambiar-contrasena")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Faltan datos obligatorios"));
    }


    


}
