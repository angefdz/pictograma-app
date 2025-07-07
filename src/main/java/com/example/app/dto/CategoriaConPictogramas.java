package com.example.app.dto;

import java.util.List;

public class CategoriaConPictogramas {

    private Long id;
    private String nombre;
    private String imagen;
    private List<PictogramaSimple> pictogramas;
    private Long usuarioId; // Cambiado

    public CategoriaConPictogramas() {}

    public CategoriaConPictogramas(Long id, String nombre, String imagen, List<PictogramaSimple> pictogramas, Long usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.pictogramas = pictogramas;
        this.usuarioId = usuarioId;
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public List<PictogramaSimple> getPictogramas() { return pictogramas; }
    public Long getUsuarioId() { return usuarioId; }

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setPictogramas(List<PictogramaSimple> pictogramas) { this.pictogramas = pictogramas; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
