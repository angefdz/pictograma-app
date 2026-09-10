package com.example.app.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrediccionSimpleTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializaElIdentificadorDevueltoPorElModelo() throws Exception {
        PrediccionSimple prediccion = objectMapper.readValue(
                "{\"sugerencia\":\"Agua\",\"pictograma_id\":202,\"alternativas\":["
                        + "{\"sugerencia\":\"Agua\",\"pictograma_id\":202,\"confianza\":0.8}]}",
                PrediccionSimple.class
        );

        assertEquals("Agua", prediccion.getSugerencia());
        assertEquals(202L, prediccion.getPictogramaId());
        assertEquals(1, prediccion.getAlternativas().size());
        assertEquals(0.8, prediccion.getAlternativas().get(0).getConfianza());
    }
}
