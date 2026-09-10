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
import java.util.LinkedHashSet;

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

    private List<Long> sugerenciasPorDefecto() {
        return List.of(84L);
    }

    public List<Long> obtenerSugerencias(String frase, String pictogramas, String texto, String idioma) {
        if (pictogramas == null || pictogramas.isBlank()) {
            return sugerenciasPorDefecto();
        }
        PrediccionSimple respuesta = solicitarPrediccion(frase, pictogramas, texto, idioma);
        if (respuesta == null) {
            return List.of();
        }
        List<PrediccionSimple> candidatas = respuesta.getAlternativas();
        if (candidatas == null || candidatas.isEmpty()) {
            candidatas = List.of(respuesta);
        }
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        for (PrediccionSimple candidata : candidatas) {
            Long id = candidata.getPictogramaId();
            if (id != null && pictogramaRepository.existsById(id)) {
                ids.add(id);
            }
            if (ids.size() == 3) break;
        }
        return List.copyOf(ids);
    }

    public String obtenerSugerencia(String frase, String pictogramas, String idioma) {
        return obtenerSugerencia(frase, pictogramas, frase, idioma);
    }

    public String obtenerSugerencia(String frase, String pictogramas, String texto, String idioma) {
        if (frase == null || frase.isBlank()) {
            return sugerenciaPorDefecto(idioma);
        }
        PrediccionSimple respuesta = solicitarPrediccion(frase, pictogramas, texto, idioma);
        String sugerencia = respuesta != null ? respuesta.getSugerencia() : null;
        Long pictogramaId = respuesta != null ? respuesta.getPictogramaId() : null;
        return sugerencia != null && !sugerencia.isBlank() && pictogramaId != null
                && pictogramaRepository.existsById(pictogramaId) ? sugerencia : sugerenciaPorDefecto(idioma);
    }

    private PrediccionSimple solicitarPrediccion(String frase, String pictogramas, String texto, String idioma) {
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
            return null;
        }
        return respuesta.getBody();
    }
}
