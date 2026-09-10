package com.example.app.dto;
import java.util.Map;

public class PictogramaSimple {

    private Long id;
    private String nombre;
    private String imagen;
    private String tipo; 
    private Map<String, String> traducciones;

    public PictogramaSimple() {}

    public PictogramaSimple(Long id, String nombre, String imagen, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public String getTipo() { return tipo; } 

    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Map<String, String> getTraducciones() { return traducciones; }
    public void setTraducciones(Map<String, String> traducciones) { this.traducciones = traducciones; }
}
