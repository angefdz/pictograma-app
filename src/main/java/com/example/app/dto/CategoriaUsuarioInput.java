package com.example.app.dto;

import java.util.List;

public class CategoriaUsuarioInput {

    private String nombre;
    private String imagen;
    private Long usuarioId;
    private List<Long> pictogramas; // IDs de pictogramas

    public CategoriaUsuarioInput() {}

    public CategoriaUsuarioInput(String nombre, String imagen, Long usuarioId, List<Long> pictogramas) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.usuarioId = usuarioId;
        this.pictogramas = pictogramas;
    }

    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public Long getUsuarioId() { return usuarioId; }
    public List<Long> getPictogramas() { return pictogramas; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public void setPictogramas(List<Long> pictogramas) { this.pictogramas = pictogramas; }
}
