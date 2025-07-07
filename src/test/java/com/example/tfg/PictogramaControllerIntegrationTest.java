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

public class PictogramaControllerIntegrationTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
	@Test
	void crearPictogramaPersonalizadoConDatosValidos() throws Exception {
	    // 1. Registrar usuario
	    Usuario usuario = new Usuario("Tester", "pictotest@ejemplo.com", "ClaveValida1!", "manual");
	    mockMvc.perform(post("/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(usuario)))
	            .andExpect(status().isOk());

	    // 2. Login para obtener token
	    String loginBody = objectMapper.writeValueAsString(Map.of(
	            "email", "pictotest@ejemplo.com",
	            "contrasena", "ClaveValida1!"
	    ));
	    String loginResponse = mockMvc.perform(post("/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(loginBody))
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // 3. Crear pictograma personalizado
	    Map<String, Object> pictogramaInput = Map.of(
	        "nombre", "comer",
	        "imagen", "https://example.com/imagen.png",
	        "tipo", "verbo",
	        "categoriasIds", List.of()  // puedes probar también con IDs si ya tienes alguno
	    );

	    mockMvc.perform(post("/pictogramas")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(pictogramaInput)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.nombre").value("comer"));
	}
	
	@Test
	void errorCrearPictogramaSinAutenticacion() throws Exception {
	    Map<String, Object> pictogramaInput = Map.of(
	        "nombre", "jugar",
	        "imagen", "https://example.com/jugar.png",
	        "tipo", "verbo",
	        "categoriasIds", List.of()
	    );

	    mockMvc.perform(post("/pictogramas")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(pictogramaInput)))
	            .andExpect(status().isUnauthorized());
	}

	@Test
	void errorCrearPictogramaConCamposFaltantes() throws Exception {
	    // Registrar y loguear al usuario
	    Usuario usuario = new Usuario("FaltanCampos", "faltapicto@ejemplo.com", "Clave123@", "manual");

	    mockMvc.perform(post("/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(usuario)))
	            .andExpect(status().isOk());

	    String loginBody = objectMapper.writeValueAsString(Map.of(
	        "email", "faltapicto@ejemplo.com",
	        "contrasena", "Clave123@"
	    ));
	    String loginResponse = mockMvc.perform(post("/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(loginBody))
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear pictograma sin nombre ni tipo
	    Map<String, Object> pictogramaIncompleto = Map.of(
	        "imagen", "https://example.com/sin-nombre.png",
	        "categoriasIds", List.of()
	    );

	    mockMvc.perform(post("/pictogramas")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(pictogramaIncompleto)))
	            .andExpect(status().isBadRequest());
	}

	
	@Test
	void actualizarPictogramaCorrectamente() throws Exception {
	    Usuario usuario = new Usuario("Editor", "editor@ejemplo.com", "Clave123@", "manual");

	    mockMvc.perform(post("/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(usuario)))
	            .andExpect(status().isOk());

	    String loginBody = objectMapper.writeValueAsString(Map.of(
	        "email", "editor@ejemplo.com",
	        "contrasena", "Clave123@"
	    ));
	    String loginResponse = mockMvc.perform(post("/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(loginBody))
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear pictograma
	    Map<String, Object> pictogramaInput = Map.of(
	        "nombre", "comer",
	        "imagen", "https://example.com/comer.png",
	        "tipo", "verbo",
	        "categoriasIds", List.of()
	    );

	    String createResponse = mockMvc.perform(post("/pictogramas")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(pictogramaInput)))
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    Long pictogramaId = objectMapper.readTree(createResponse).get("id").asLong();

	    // Actualizar pictograma
	    Map<String, Object> updateInput = Map.of(
	        "nombre", "beber",
	        "imagen", "https://example.com/beber.png",
	        "tipo", "verbo",
	        "categoriasIds", List.of()
	    );

	    mockMvc.perform(put("/pictogramas/" + pictogramaId)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateInput)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.nombre").value("beber"))
	            .andExpect(jsonPath("$.imagen").value("https://example.com/beber.png"));
	}

	@Test
	void actualizarPictogramaSinAutenticacionDevuelve401() throws Exception {
	    Map<String, Object> updateInput = Map.of(
	        "nombre", "nuevoNombre",
	        "imagen", "https://example.com/nueva.png",
	        "tipo", "verbo",
	        "categoriasIds", List.of()
	    );

	    mockMvc.perform(put("/pictogramas/1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateInput)))
	            .andExpect(status().isUnauthorized());
	}

	@Test
	void actualizarPictogramaConCamposIncompletosDevuelve400() throws Exception {
	    Usuario usuario = new Usuario("Campos", "campos@ejemplo.com", "Clave123@", "manual");

	    mockMvc.perform(post("/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(usuario)))
	            .andExpect(status().isOk());

	    String login = objectMapper.writeValueAsString(Map.of(
	        "email", "campos@ejemplo.com",
	        "contrasena", "Clave123@"
	    ));
	    String response = mockMvc.perform(post("/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(login))
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(response).get("token").asText();

	    // Crear pictograma válido
	    Map<String, Object> pictograma = Map.of(
	        "nombre", "mirar",
	        "imagen", "https://example.com/mirar.png",
	        "tipo", "verbo",
	        "categoriasIds", List.of()
	    );

	    String creado = mockMvc.perform(post("/pictogramas")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(pictograma)))
	            .andReturn().getResponse().getContentAsString();

	    Long pictogramaId = objectMapper.readTree(creado).get("id").asLong();

	    // Actualización sin tipo
	    Map<String, Object> updateInput = Map.of(
	        "nombre", "mirar de nuevo",
	        "imagen", "https://example.com/mirar2.png",
	        "categoriasIds", List.of()
	    );

	    mockMvc.perform(put("/pictogramas/" + pictogramaId)
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateInput)))
	            .andExpect(status().isBadRequest());
	}
	
	@Test
	void eliminarPictogramaCorrectamente() throws Exception {
	    Usuario usuario = new Usuario("Eliminar", "eliminar@ejemplo.com", "Clave123@", "manual");

	    // Registrar y loguear
	    mockMvc.perform(post("/auth/register")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(usuario)))
	        .andExpect(status().isOk());

	    String loginResponse = mockMvc.perform(post("/auth/login")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(Map.of(
	            "email", "eliminar@ejemplo.com",
	            "contrasena", "Clave123@"
	        )))).andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear pictograma
	    String createResponse = mockMvc.perform(post("/pictogramas")
	        .header("Authorization", "Bearer " + token)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(Map.of(
	            "nombre", "romper",
	            "imagen", "https://example.com/romper.png",
	            "tipo", "verbo",
	            "categoriasIds", List.of()
	        )))).andExpect(status().isOk())
	        .andReturn().getResponse().getContentAsString();

	    Long id = objectMapper.readTree(createResponse).get("id").asLong();

	    // Eliminar pictograma
	    mockMvc.perform(delete("/pictogramas/" + id)
	        .header("Authorization", "Bearer " + token))
	        .andExpect(status().isNoContent());
	}

	@Test
	void errorEliminarPictogramaSinAutenticacion() throws Exception {
	    mockMvc.perform(delete("/pictogramas/1"))
	        .andExpect(status().isUnauthorized());
	}

	@Test
	void errorEliminarPictogramaDeOtroUsuario() throws Exception {
	    // Usuario 1 crea pictograma
	    Usuario usuario1 = new Usuario("User1", "user1@ejemplo.com", "Clave123@", "manual");
	    mockMvc.perform(post("/auth/register")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(usuario1)))
	        .andExpect(status().isOk());

	    String token1 = objectMapper.readTree(mockMvc.perform(post("/auth/login")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(Map.of("email", "user1@ejemplo.com", "contrasena", "Clave123@"))))
	        .andReturn().getResponse().getContentAsString())
	        .get("token").asText();

	    String createResponse = mockMvc.perform(post("/pictogramas")
	        .header("Authorization", "Bearer " + token1)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(Map.of(
	            "nombre", "saltar",
	            "imagen", "https://example.com/saltar.png",
	            "tipo", "verbo",
	            "categoriasIds", List.of()
	        )))).andReturn().getResponse().getContentAsString();

	    Long id = objectMapper.readTree(createResponse).get("id").asLong();

	    // Usuario 2 intenta eliminar
	    Usuario usuario2 = new Usuario("User2", "user2@ejemplo.com", "Clave123@", "manual");
	    mockMvc.perform(post("/auth/register")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(usuario2)))
	        .andExpect(status().isOk());

	    String token2 = objectMapper.readTree(mockMvc.perform(post("/auth/login")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(Map.of("email", "user2@ejemplo.com", "contrasena", "Clave123@"))))
	        .andReturn().getResponse().getContentAsString())
	        .get("token").asText();

	    mockMvc.perform(delete("/pictogramas/" + id)
	        .header("Authorization", "Bearer " + token2))
	        .andExpect(status().isForbidden());
	}

	@Test
	void errorEliminarPictogramaInexistente() throws Exception {
	    Usuario usuario = new Usuario("Inexistente", "inexistente@ejemplo.com", "Clave123@", "manual");

	    mockMvc.perform(post("/auth/register")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(usuario)))
	        .andExpect(status().isOk());

	    String token = objectMapper.readTree(mockMvc.perform(post("/auth/login")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(Map.of("email", "inexistente@ejemplo.com", "contrasena", "Clave123@"))))
	        .andReturn().getResponse().getContentAsString())
	        .get("token").asText();

	    mockMvc.perform(delete("/pictogramas/999999")  // ID que no existe
	        .header("Authorization", "Bearer " + token))
	        .andExpect(status().isNotFound());
	}

	@Test
	void consultarPictogramaConCategorias_correctamente() throws Exception {
	    Usuario usuario = new Usuario("Consulta", "consulta@ejemplo.com", "Clave123@", "manual");

	    // Registrar y loguear usuario
	    mockMvc.perform(post("/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(usuario)))
	            .andExpect(status().isOk());

	    String login = objectMapper.writeValueAsString(Map.of(
	            "email", "consulta@ejemplo.com",
	            "contrasena", "Clave123@"
	    ));

	    String loginResponse = mockMvc.perform(post("/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(login))
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear pictograma
	    Map<String, Object> pictograma = Map.of(
	            "nombre", "leer",
	            "imagen", "https://example.com/leer.png",
	            "tipo", "verbo",
	            "categoriasIds", List.of()
	    );

	    String creado = mockMvc.perform(post("/pictogramas")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(pictograma)))
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    Long pictogramaId = objectMapper.readTree(creado).get("id").asLong();

	    // Consultar pictograma con categorías
	    mockMvc.perform(get("/pictogramas/" + pictogramaId + "/con-categorias")
	            .header("Authorization", "Bearer " + token))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.nombre").value("leer"))
	            .andExpect(jsonPath("$.tipo").value("verbo"));
	}

	@Test
	void errorConsultarPictogramaConCategorias_sinAutenticacion() throws Exception {
	    mockMvc.perform(get("/pictogramas/1/con-categorias"))
	            .andExpect(status().isUnauthorized());
	}

	@Test
	void errorConsultarPictogramaConCategorias_inexistente() throws Exception {
	    Usuario usuario = new Usuario("Consulta2", "consulta2@ejemplo.com", "Clave123@", "manual");

	    // Registrar y loguear usuario
	    mockMvc.perform(post("/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(usuario)))
	            .andExpect(status().isOk());

	    String login = objectMapper.writeValueAsString(Map.of(
	            "email", "consulta2@ejemplo.com",
	            "contrasena", "Clave123@"
	    ));

	    String loginResponse = mockMvc.perform(post("/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(login))
	            .andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Consultar pictograma inexistente
	    mockMvc.perform(get("/pictogramas/99999/con-categorias")
	            .header("Authorization", "Bearer " + token))
	            .andExpect(status().isNotFound());
	}


	@Test
	void obtenerPictogramasVisibles_correcto() throws Exception {
	    // Registrar usuario y loguear
	    Usuario usuario = new Usuario("Visible", "visible@ejemplo.com", "Clave123@", "manual");
	    mockMvc.perform(post("/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(usuario)))
	            .andExpect(status().isOk());

	    String loginResponse = mockMvc.perform(post("/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(Map.of(
	                "email", "visible@ejemplo.com",
	                "contrasena", "Clave123@"
	            )))).andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Crear pictograma personalizado
	    mockMvc.perform(post("/pictogramas")
	            .header("Authorization", "Bearer " + token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(Map.of(
	                "nombre", "leer",
	                "imagen", "https://example.com/leer.png",
	                "tipo", "verbo",
	                "categoriasIds", List.of()
	            )))).andExpect(status().isOk());

	    // Obtener pictogramas visibles
	    mockMvc.perform(get("/pictogramas")
	            .header("Authorization", "Bearer " + token))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$").isArray())
	            .andExpect(jsonPath("$.length()").value(1))
	            .andExpect(jsonPath("$[0].nombre").value("leer"));
	}

	@Test
	void obtenerPictogramasVisibles_sinResultados() throws Exception {
	    // Registrar y loguear usuario que no tiene pictogramas
	    Usuario usuario = new Usuario("Vacio", "vacio@ejemplo.com", "Clave123@", "manual");
	    mockMvc.perform(post("/auth/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(usuario)))
	            .andExpect(status().isOk());

	    String loginResponse = mockMvc.perform(post("/auth/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(Map.of(
	                "email", "vacio@ejemplo.com",
	                "contrasena", "Clave123@"
	            )))).andExpect(status().isOk())
	            .andReturn().getResponse().getContentAsString();

	    String token = objectMapper.readTree(loginResponse).get("token").asText();

	    // Consultar pictogramas visibles (no tiene ninguno)
	    mockMvc.perform(get("/pictogramas")
	            .header("Authorization", "Bearer " + token))
	            .andExpect(status().isNoContent());
	}

	@Test
	void obtenerPictogramasVisibles_sinAutenticacion() throws Exception {
	    mockMvc.perform(get("/pictogramas"))
	            .andExpect(status().isUnauthorized());
	}


}
