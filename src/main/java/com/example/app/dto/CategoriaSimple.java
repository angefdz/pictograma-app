package com.example.app.dto;

public class CategoriaSimple {

    private Long id;
    private String nombre;
    private String imagen;
    private Long usuarioId; // 👈 Añadido

    public CategoriaSimple() {}

    public CategoriaSimple(Long id, String nombre, String imagen, Long usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.usuarioId = usuarioId;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
