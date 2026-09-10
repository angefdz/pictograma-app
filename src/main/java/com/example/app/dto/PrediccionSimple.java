package com.example.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PrediccionSimple {
    private String sugerencia;

    @JsonProperty("pictograma_id")
    private Long pictogramaId;

    private Double confianza;
    private List<PrediccionSimple> alternativas;

    public String getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia;
    }

    public Long getPictogramaId() { return pictogramaId; }
    public void setPictogramaId(Long pictogramaId) { this.pictogramaId = pictogramaId; }
    public Double getConfianza() { return confianza; }
    public void setConfianza(Double confianza) { this.confianza = confianza; }
    public List<PrediccionSimple> getAlternativas() { return alternativas; }
    public void setAlternativas(List<PrediccionSimple> alternativas) { this.alternativas = alternativas; }
}
