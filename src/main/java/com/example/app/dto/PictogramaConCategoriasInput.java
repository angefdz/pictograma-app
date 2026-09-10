package com.example.app.dto;

import java.util.List;
import java.util.Map;

public class PictogramaConCategoriasInput {

    private String nombre;
    private String tipo;
    private String imagen;
    private Long usuario; 
    private List<Long> categorias; 
    private Map<String, String> traducciones;

    public PictogramaConCategoriasInput() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    public List<Long> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Long> categorias) {
        this.categorias = categorias;
    }
    public Map<String, String> getTraducciones() { return traducciones; }
    public void setTraducciones(Map<String, String> traducciones) { this.traducciones = traducciones; }
}
