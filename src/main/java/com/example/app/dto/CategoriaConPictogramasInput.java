package com.example.app.dto;

import java.util.List;
import java.util.Map;

public class CategoriaConPictogramasInput {

    private String nombre;
    private String imagen;
    private List<Long> pictogramas;
    private Map<String, String> traducciones;

    public CategoriaConPictogramasInput() {}

    public CategoriaConPictogramasInput(String nombre, String imagen, List<Long> pictogramas) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.pictogramas = pictogramas;
    }

    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public List<Long> getPictogramas() { return pictogramas; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setPictogramas(List<Long> pictogramas) { this.pictogramas = pictogramas; }
    public Map<String, String> getTraducciones() { return traducciones; }
    public void setTraducciones(Map<String, String> traducciones) { this.traducciones = traducciones; }
}
