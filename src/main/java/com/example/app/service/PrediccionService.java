package com.example.app.service;

import com.example.app.dto.PrediccionSimple;
import com.example.app.model.Pictograma;
import com.example.app.repository.PictogramaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PrediccionService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PictogramaRepository pictogramaRepository;

    private static final String URL_FASTAPI = "http://modelo:8000/predecir";
    private static final String DEFAULT_SUGERENCIA = "Hola";

    public String obtenerSugerencia(String frase) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> cuerpo = Map.of("frase", frase);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(cuerpo, headers);

        ResponseEntity<PrediccionSimple> respuesta = restTemplate.postForEntity(
                URL_FASTAPI, request, PrediccionSimple.class);

        String sugerencia = respuesta.getBody() != null ? respuesta.getBody().getSugerencia() : null;

        if (sugerencia != null && pictogramaRepository.existsPictogramaGeneralByNombre(sugerencia)) {
            return sugerencia;
        } else {
            return DEFAULT_SUGERENCIA;
    }
    }
   }
