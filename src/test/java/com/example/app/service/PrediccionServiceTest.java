package com.example.app.service;

import com.example.app.repository.PictogramaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PrediccionServiceTest {

    private PrediccionService service;
    private MockRestServiceServer servidorModelo;
    private PictogramaRepository pictogramaRepository;

    @BeforeEach
    void preparar() {
        RestTemplate restTemplate = new RestTemplate();
        servidorModelo = MockRestServiceServer.bindTo(restTemplate).build();
        pictogramaRepository = mock(PictogramaRepository.class);

        service = new PrediccionService();
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "pictogramaRepository", pictogramaRepository);
        ReflectionTestUtils.setField(service, "urlFastApi", "http://modelo:8000/predecir");
    }

    @Test
    void conservaLaPrediccionValidaDelModelo() {
        when(pictogramaRepository.existsById(202L)).thenReturn(true);
        servidorModelo.expect(once(), requestTo("http://modelo:8000/predecir"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {"pictograma_ids":[10,11],"lemas":["yo","querer"],"texto":"yo\u001fquerer","idioma":"es"}
                        """))
                .andRespond(withSuccess(
                        "{\"sugerencia\":\"Agua\",\"pictograma_id\":202,\"confianza\":0.8}",
                        MediaType.APPLICATION_JSON
                ));

        String resultado = service.obtenerSugerencia("yo\u001fquerer", "10,11", "es");

        assertEquals("Agua", resultado);
        servidorModelo.verify();
    }

    @Test
    void devuelveTresIdsValidosEnElOrdenDelModelo() {
        when(pictogramaRepository.existsById(202L)).thenReturn(true);
        when(pictogramaRepository.existsById(1L)).thenReturn(true);
        when(pictogramaRepository.existsById(4L)).thenReturn(true);
        servidorModelo.expect(once(), requestTo("http://modelo:8000/predecir"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"pictograma_id":202,"sugerencia":"Agua","alternativas":[
                          {"pictograma_id":202,"sugerencia":"Agua","confianza":0.5},
                          {"pictograma_id":1,"sugerencia":"Comer","confianza":0.3},
                          {"pictograma_id":4,"sugerencia":"Jugar","confianza":0.2}
                        ]}
                        """, MediaType.APPLICATION_JSON));

        List<Long> resultado = service.obtenerSugerencias(
                "yo\u001fquerer", "84,43", "Yo quiero", "es");

        assertEquals(List.of(202L, 1L, 4L), resultado);
        servidorModelo.verify();
    }
}
