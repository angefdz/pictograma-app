package com.example.app.model;

import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "frases")
public class Frase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idFrase;

    @NotNull
    @NotEmpty
    @Column
    private String texto;

    @NotNull
    @Column
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Frase(Long idFrase, String texto, Date fecha, Usuario usuario) {
        this.idFrase = idFrase;
        this.texto = texto;
        this.fecha = fecha;
        this.usuario = usuario;
    }

    public Frase(String texto, Date fecha, Usuario usuario) {
        this.texto = texto;
        this.fecha = fecha;
        this.usuario = usuario;
    }

    public Frase() {}

    public Long getIdFrase() {
        return idFrase;
    }

    public void setIdFrase(Long idFrase) {
        this.idFrase = idFrase;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
