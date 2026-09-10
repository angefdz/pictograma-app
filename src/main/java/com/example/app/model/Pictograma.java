package com.example.app.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.MapKeyColumn;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pictogramas")
public class Pictograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id; 

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = true) 
    private String imagen;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String tipo;

    @ElementCollection
    @CollectionTable(name = "pictograma_traducciones", joinColumns = @JoinColumn(name = "pictograma_id"))
    @MapKeyColumn(name = "idioma")
    @Column(name = "nombre", nullable = false)
    private Map<String, String> traducciones = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;


    public Pictograma() {}

    public Pictograma(String nombre, String imagen, String tipo) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.tipo = tipo;
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

    public String getTipo() {
        return tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Map<String, String> getTraducciones() { return traducciones; }
    public void setTraducciones(Map<String, String> traducciones) {
        this.traducciones = traducciones == null ? new HashMap<>() : traducciones;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
}
