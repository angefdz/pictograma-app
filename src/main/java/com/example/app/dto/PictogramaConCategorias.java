package com.example.app.dto;

import java.util.List;

public class PictogramaConCategorias {
    private Long id;
    private String nombre;
    private String imagen;
    private String tipo;
    private Long usuarioId; 
    private List<CategoriaSimple> categorias;

    public PictogramaConCategorias() {}

    public PictogramaConCategorias(Long id, String nombre, String imagen, String tipo, Long usuarioId, List<CategoriaSimple> categorias) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.categorias = categorias;
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public String getTipo() { return tipo; }
    public Long getUsuarioId() { return usuarioId; }
    public List<CategoriaSimple> getCategorias() { return categorias; }

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public void setCategorias(List<CategoriaSimple> categorias) { this.categorias = categorias; }
}
