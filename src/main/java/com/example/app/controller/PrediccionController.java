package com.example.app.controller;

import com.example.app.service.PrediccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prediccion")
public class PrediccionController {

    @Autowired
    private PrediccionService prediccionService;

    @GetMapping
    public String sugerir(@RequestParam(required = false) String frase,
                         @RequestParam(required = false) String lemas,
                         @RequestParam(required = false) String texto,
                         @RequestParam(required = false) String pictogramas,
                         @RequestParam(defaultValue = "es") String idioma) {
        String entrada = lemas != null && !lemas.isBlank() ? lemas : frase;
        return prediccionService.obtenerSugerencia(entrada, pictogramas, texto, idioma);
    } 
}
