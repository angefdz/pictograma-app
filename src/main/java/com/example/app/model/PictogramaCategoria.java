package com.example.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pictogramas_categoria")
public class PictogramaCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Usuario que ha creado esta relación (nullable solo si la relación es general)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pictograma_id", nullable = false)
    private Pictograma pictograma;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public PictogramaCategoria() {}

    public PictogramaCategoria(Usuario usuario, Pictograma pictograma, Categoria categoria) {
        this.usuario = usuario;
        this.pictograma = pictograma;
        this.categoria = categoria;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setPictograma(Pictograma pictograma) {
        this.pictograma = pictograma;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
