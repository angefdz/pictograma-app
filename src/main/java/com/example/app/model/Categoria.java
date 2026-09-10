package com.example.app.model;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String nombre;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String imagen;

    @ElementCollection
    @CollectionTable(name = "categoria_traducciones", joinColumns = @JoinColumn(name = "categoria_id"))
    @MapKeyColumn(name = "idioma")
    @Column(name = "nombre", nullable = false)
    private Map<String, String> traducciones = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    public Categoria() {}

    public Categoria(String nombre, String imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
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

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
