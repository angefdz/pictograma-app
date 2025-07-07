package com.example.tfg;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.example.app.TfgApplication;
import com.example.app.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper; 

@SpringBootTest(classes = TfgApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")

public class CategoriaControllerIntegrationTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void crearCategoriaPersonalizada_correctamente() throws Exception {
        // Registrar y loguear usuario
        Usuario usuario = new Usuario("Categoria", "categoria@ejemplo.com", "Clave123@", "manual");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        String loginBody = objectMapper.writeValueAsString(Map.of(
            "email", "categoria@ejemplo.com",
            "contrasena", "Clave123@"
        ));
        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Crear categoría
        Map<String, Object> categoriaInput = Map.of(
            "nombre", "Animales",
            "imagen", "https://example.com/animal.png",
            "pictogramasIds", List.of()
        );

        mockMvc.perform(post("/categorias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoriaInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Animales"));
    }

    @Test
    void errorCrearCategoriaSinAutenticacion() throws Exception {
        Map<String, Object> categoriaInput = Map.of(
            "nombre", "Frutas",
            "imagen", "https://example.com/frutas.png",
            "pictogramasIds", List.of()
        );

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoriaInput)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void errorCrearCategoriaConDatosInvalidos() throws Exception {
        // Registrar y loguear usuario
        Usuario usuario = new Usuario("Categoria2", "categoria2@ejemplo.com", "Clave123@", "manual");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        String loginBody = objectMapper.writeValueAsString(Map.of(
            "email", "categoria2@ejemplo.com",
            "contrasena", "Clave123@"
        ));
        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Crear categoría sin nombre
        Map<String, Object> categoriaInput = Map.of(
            "imagen", "https://example.com/frutas.png",
            "pictogramasIds", List.of()
        );

        mockMvc.perform(post("/categorias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoriaInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editarCategoria_sinAutenticacion_devuelve401() throws Exception {
        Map<String, Object> updateInput = Map.of(
                "nombre", "Naturaleza",
                "imagen", "https://example.com/naturaleza.png",
                "pictogramasIds", List.of()
        );

        mockMvc.perform(put("/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateInput)))
                .andExpect(status().isUnauthorized());
    }

  

    @Test
    void editarCategoria_camposInvalidos_devuelve400() throws Exception {
        Usuario usuario = new Usuario("CatInvalida", "catinvalida@ejemplo.com", "Clave123@", "manual");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk());

        String login = objectMapper.writeValueAsString(Map.of(
                "email", "catinvalida@ejemplo.com",
                "contrasena", "Clave123@"
        ));

        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Crear categoría
        Map<String, Object> categoria = Map.of(
                "nombre", "Ropa",
                "imagen", "https://example.com/ropa.png",
                "pictogramasIds", List.of()
        );

        String creada = mockMvc.perform(post("/categorias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andReturn().getResponse().getContentAsString();

        Long categoriaId = objectMapper.readTree(creada).get("id").asLong();

        // Intentar actualizar sin nombre
        Map<String, Object> actualizacionInvalida = Map.of(
                "imagen", "https://example.com/ropa-nueva.png",
                "pictogramasIds", List.of()
        );

        mockMvc.perform(put("/categorias/" + categoriaId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizacionInvalida)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void errorEliminarCategoriaSinAutenticacion() throws Exception {
        mockMvc.perform(delete("/categorias/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void errorEliminarCategoriaDeOtroUsuario() throws Exception {
        // Usuario 1 crea la categoría
        Usuario usuario1 = new Usuario("CatUser1", "catuser1@ejemplo.com", "Clave123@", "manual");
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(usuario1)))
            .andExpect(status().isOk());

        String token1 = objectMapper.readTree(mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of(
                "email", "catuser1@ejemplo.com",
                "contrasena", "Clave123@"
            )))).andReturn().getResponse().getContentAsString())
            .get("token").asText();

        Map<String, Object> categoriaInput = Map.of(
            "nombre", "Animales",
            "imagen", "https://example.com/animal.png",
            "pictogramasIds", List.of()
        );

        String response = mockMvc.perform(post("/categorias")
            .header("Authorization", "Bearer " + token1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoriaInput)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        Long idCategoria = objectMapper.readTree(response).get("id").asLong();

        // Usuario 2 intenta eliminarla
        Usuario usuario2 = new Usuario("CatUser2", "catuser2@ejemplo.com", "Clave123@", "manual");
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(usuario2)))
            .andExpect(status().isOk());

        String token2 = objectMapper.readTree(mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of(
                "email", "catuser2@ejemplo.com",
                "contrasena", "Clave123@"
            )))).andReturn().getResponse().getContentAsString())
            .get("token").asText();

        mockMvc.perform(delete("/categorias/" + idCategoria)
            .header("Authorization", "Bearer " + token2))
            .andExpect(status().isForbidden());
    }

    @Test
    void errorEliminarCategoriaInexistente() throws Exception {
        Usuario usuario = new Usuario("CatUser3", "catuser3@ejemplo.com", "Clave123@", "manual");

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isOk());

        String token = objectMapper.readTree(mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of(
                "email", "catuser3@ejemplo.com",
                "contrasena", "Clave123@"
            )))).andReturn().getResponse().getContentAsString())
            .get("token").asText();

        mockMvc.perform(delete("/categorias/999999")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }



    @Test
    void obtenerCategoriasConPictogramas_usuarioSinCategorias_devuelveNoContent() throws Exception {
        Usuario usuario = new Usuario("Vacio", "vacio@correo.com", "Clave123@", "manual");
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isOk());

        String token = objectMapper.readTree(mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of(
                "email", "vacio@correo.com",
                "contrasena", "Clave123@"
            )))).andReturn().getResponse().getContentAsString())
            .get("token").asText();

        mockMvc.perform(get("/categorias/con-pictogramas")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerCategoriasConPictogramas_sinAutenticacion_devuelveUnauthorized() throws Exception {
        mockMvc.perform(get("/categorias/con-pictogramas"))
            .andExpect(status().isUnauthorized());
    }

    
}
