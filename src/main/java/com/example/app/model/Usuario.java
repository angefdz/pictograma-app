package com.example.app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "nombre")
    private String nombre;

    @NotEmpty
    @NotNull
    @Column(name = "correo", unique = true)
    private String email;

    @Column(name = "metodo_autenticacion")  // nuevo campo
    private String metodoAutenticacion;

    @Column(name = "contrasena_hash")
    private String contrasena;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PictogramaOculto> pictogramasOcultos = new ArrayList<>();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Configuracion configuracion;

    public Usuario() {}

    // Constructor con nuevo campo añadido
    public Usuario(Long id, String nombre, String email, String contrasenaHash, String metodoAutenticacion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasenaHash;
        this.metodoAutenticacion = metodoAutenticacion;
    }

    // Puedes tener constructor sin id si quieres
    public Usuario(String nombre, String email, String contrasenaHash, String metodoAutenticacion) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasenaHash;
        this.metodoAutenticacion = metodoAutenticacion;
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMetodoAutenticacion() {
        return metodoAutenticacion;
    }

    public void setMetodoAutenticacion(String metodoAutenticacion) {
        this.metodoAutenticacion = metodoAutenticacion;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasenaHash) {
        this.contrasena = contrasenaHash;
    }

    public List<PictogramaOculto> getPictogramasOcultos() {
        return pictogramasOcultos;
    }

    public void setPictogramasOcultos(List<PictogramaOculto> pictogramasOcultos) {
        this.pictogramasOcultos = pictogramasOcultos;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }
}
