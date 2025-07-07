// src/main/java/com/example/app/controller/PrediccionController.java
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
    public String sugerir(@RequestParam String frase) {
        return prediccionService.obtenerSugerencia(frase);
    }
}
