package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.model.Frase;
import com.example.app.model.Usuario;
import com.example.app.repository.UsuarioRepository;
import com.example.app.service.FraseService;

@RestController
@RequestMapping("/frases")
public class FraseController {
	
	@Autowired
	private FraseService fraseService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	private String getCorreoAutenticado() {
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	    if (principal instanceof Usuario usuario) {
	        return usuario.getEmail();
	    }

	    return principal.toString();
	}

	@PostMapping
	public ResponseEntity<Frase> createFrase(@RequestBody Frase f) {
	    String correo = getCorreoAutenticado();
	    Frase nuevaFrase = fraseService.crearFrase(f, usuarioRepository.buscarPorEmail(correo).get());
	    return new ResponseEntity<>(nuevaFrase, HttpStatus.CREATED);
	}


	@GetMapping(value = "/descargar", produces = "text/csv;charset=UTF-8")
	@ResponseBody
	public ResponseEntity<String> descargarHistorialCsv() {
	    String correo = getCorreoAutenticado();
	    Usuario usuario = usuarioRepository.buscarPorEmail(correo).orElse(null);

	    if (usuario == null) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }

	    List<Frase> frases = fraseService.obtenerFrasesByUsuario(usuario.getId());

	    if (frases.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }

	    // Construir el contenido CSV (sin id_frase)
	    StringBuilder csvBuilder = new StringBuilder();
	    csvBuilder.append("texto,fecha\n");

	    for (Frase f : frases) {
	        csvBuilder.append("\"").append(f.getTexto().replace("\"", "\"\"")).append("\",");
	        csvBuilder.append(f.getFecha()).append("\n");
	    }

	    return ResponseEntity.ok()
	        .header("Content-Disposition", "attachment; filename=historial_frases.csv")
	        .body(csvBuilder.toString());
	}

}
