package com.example.app.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.model.Frase;
import com.example.app.model.Usuario;
import com.example.app.repository.FraseRepository;

@Service
public class FraseService {
	
	@Autowired
	private FraseRepository fraseRepository;
	
	public Frase crearFrase(Frase frase, Usuario usuario) {
		if (frase.getFecha() == null) {
	        frase.setFecha(new Date());
	        frase.setUsuario(usuario);// o LocalDate.now() si usas solo la fecha
	    }
		return fraseRepository.save(frase);
	}
	
	public List<Frase> obtenerFrasesByUsuario(Long usuarioId){
		return fraseRepository.findByUsuarioId(usuarioId);
	}

}

