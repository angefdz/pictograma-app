package com.example.app.service;

import com.example.app.dto.PrediccionSimple;
import com.example.app.repository.PictogramaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.Arrays;
import java.util.List;

@Service
public class PrediccionService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PictogramaRepository pictogramaRepository;

    @Value("${prediction.service.url:http://modelo:8000/predecir}")
    private String urlFastApi;
    private String sugerenciaPorDefecto(String idioma) {
        return "en".equalsIgnoreCase(idioma) ? "Hello" : "Hola";
    }

    public String obtenerSugerencia(String frase, String pictogramas, String idioma) {
        return obtenerSugerencia(frase, pictogramas, frase, idioma);
    }

    public String obtenerSugerencia(String frase, String pictogramas, String texto, String idioma) {
        if (frase == null || frase.isBlank()) {
            return sugerenciaPorDefecto(idioma);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<String> lemas = Arrays.stream(frase.split("\\u001f")).map(String::trim).filter(lema -> !lema.isEmpty()).toList();
        List<Long> pictogramaIds = pictogramas == null || pictogramas.isBlank()
                ? List.of()
                : Arrays.stream(pictogramas.split(",")).map(String::trim).filter(valor -> !valor.isEmpty()).map(Long::valueOf).toList();
        Map<String, Object> cuerpo = Map.of("pictograma_ids", pictogramaIds, "lemas", lemas, "texto", texto == null ? "" : texto, "idioma", idioma);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(cuerpo, headers);
 
        ResponseEntity<PrediccionSimple> respuesta;
        try {
            respuesta = restTemplate.postForEntity(
                    urlFastApi, request, PrediccionSimple.class);
        } catch (RestClientException exception) {
            return sugerenciaPorDefecto(idioma);
        }

        String sugerencia = respuesta.getBody() != null ? respuesta.getBody().getSugerencia() : null;
        Long pictogramaId = respuesta.getBody() != null ? respuesta.getBody().getPictogramaId() : null;

        boolean sugerenciaValida = sugerencia != null && !sugerencia.isBlank()
                && pictogramaId != null && pictogramaRepository.existsById(pictogramaId);
        if (sugerenciaValida) {
            return sugerencia;
        } else {
            return sugerenciaPorDefecto(idioma);
        }
    }
}
