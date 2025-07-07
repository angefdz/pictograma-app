package com.example.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pictogramas_ocultos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "pictograma_id"})
})
public class PictogramaOculto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pictograma_id", nullable = false)
    private Pictograma pictograma;

    public PictogramaOculto() {}

    public PictogramaOculto(Usuario usuario, Pictograma pictograma) {
        this.usuario = usuario;
        this.pictograma = pictograma;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Pictograma getPictograma() {
        return pictograma;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setPictograma(Pictograma pictograma) {
        this.pictograma = pictograma;
    }
}
