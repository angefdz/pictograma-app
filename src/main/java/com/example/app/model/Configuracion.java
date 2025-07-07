package com.example.app.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "configuraciones")
public class Configuracion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    // Relación OneToOne con Usuario
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "botones_por_pantalla")
    private Integer botonesPorPantalla;

    @Column(name = "mostrar_por_categoria")
    private Boolean mostrarPorCategoria = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_voz", columnDefinition = "enum('masculina','femenina')")
    private TipoVoz tipoVoz = TipoVoz.femenina;

    public Configuracion() {
    }
    public Configuracion(Usuario usuario, Integer botonesPorPantalla, Boolean mostrarPorCategoria, TipoVoz tipoVoz) {
        this.usuario = usuario;
        this.botonesPorPantalla = botonesPorPantalla;
        this.mostrarPorCategoria = mostrarPorCategoria;
        this.tipoVoz = tipoVoz;
    }

    // Getters y setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getBotonesPorPantalla() {
        return botonesPorPantalla;
    }

    public void setBotonesPorPantalla(Integer botonesPorPantalla) {
        this.botonesPorPantalla = botonesPorPantalla;
    }

    public Boolean getMostrarPorCategoria() {
        return mostrarPorCategoria;
    }

    public void setMostrarPorCategoria(Boolean mostrarPorCategoria) {
        this.mostrarPorCategoria = mostrarPorCategoria;
    }

    public TipoVoz getTipoVoz() {
        return tipoVoz;
    }

    public void setTipoVoz(TipoVoz tipoVoz) {
        this.tipoVoz = tipoVoz;
    }
}
