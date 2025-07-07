package com.example.app.dto;



public class UsuarioSimple {
    private Long id;
    private String correo;
    private String nombre;

    public UsuarioSimple() {}

    public UsuarioSimple(Long id, String correo) {
        this.id = id;
        this.correo = correo;
    }

    public Long getId() { return id; }
    public String getCorreo() { return correo; }
	public String getNombre() {return nombre;}


    public void setId(Long id) { this.id = id; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setNombre(String nombre) {this.nombre = nombre;}

	
}
