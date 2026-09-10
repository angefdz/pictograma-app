package com.example.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrediccionSimple {
    private String sugerencia;

    @JsonProperty("pictograma_id")
    private Long pictogramaId;

    public String getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia;
    }

    public Long getPictogramaId() { return pictogramaId; }
    public void setPictogramaId(Long pictogramaId) { this.pictogramaId = pictogramaId; }
}
