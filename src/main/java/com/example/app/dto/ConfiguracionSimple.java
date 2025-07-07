package com.example.app.dto;

public class ConfiguracionSimple {

    private Integer id;
    private Integer botonesPorPantalla;
    private Boolean mostrarPorCategoria;
    private String tipoVoz; // Cambiado a String para el DTO
    private Long usuarioId;

    public ConfiguracionSimple() {}

    public ConfiguracionSimple(Integer id, Integer botonesPorPantalla, Boolean mostrarPorCategoria, String tipoVoz, Long usuarioId) {
        this.id = id;
        this.botonesPorPantalla = botonesPorPantalla;
        this.mostrarPorCategoria = mostrarPorCategoria;
        this.tipoVoz = tipoVoz;
        this.usuarioId = usuarioId;
    }

    public Integer getId() {
        return id;
    }

    public Integer getBotonesPorPantalla() {
        return botonesPorPantalla;
    }

    public Boolean getMostrarPorCategoria() {
        return mostrarPorCategoria;
    }

    public String getTipoVoz() { // Getter devuelve String
        return tipoVoz;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBotonesPorPantalla(Integer botonesPorPantalla) {
        this.botonesPorPantalla = botonesPorPantalla;
    }

    public void setMostrarPorCategoria(Boolean mostrarPorCategoria) {
        this.mostrarPorCategoria = mostrarPorCategoria;
    }

    public void setTipoVoz(String tipoVoz) { // Setter acepta String
        this.tipoVoz = tipoVoz;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}