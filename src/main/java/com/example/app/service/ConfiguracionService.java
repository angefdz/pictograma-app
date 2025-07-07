package com.example.app.service;

import com.example.app.model.Configuracion;
import com.example.app.repository.ConfiguracionRepository;
import com.example.app.dto.ConfiguracionSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
// No necesitas importar TipoVoz aquí a menos que lo uses directamente en otros métodos del servicio

@Service
public class ConfiguracionService {

    @Autowired
    private ConfiguracionRepository configuracionRepository;

    public Optional<ConfiguracionSimple> getConfiguracionSimpleByUsuarioId(Long usuarioId) {
        System.out.println(">>> DEBUG ConfiguracionService: Buscando configuración en repositorio para usuario ID: " + usuarioId);
        Optional<Configuracion> configuracionOptional = Optional.ofNullable(configuracionRepository.buscarPorUsuario(usuarioId));

        if (configuracionOptional.isPresent()) {
            System.out.println(">>> DEBUG ConfiguracionService: Repositorio encontró configuración.");
            return configuracionOptional.map(this::convertToSimpleDto);
        } else {
            System.out.println(">>> DEBUG ConfiguracionService: Repositorio NO encontró configuración.");
            return Optional.empty();
        }
    }

    private ConfiguracionSimple convertToSimpleDto(Configuracion configuracion) {
        ConfiguracionSimple dto = new ConfiguracionSimple();
        dto.setId(configuracion.getId());
        dto.setBotonesPorPantalla(configuracion.getBotonesPorPantalla());
        dto.setMostrarPorCategoria(configuracion.getMostrarPorCategoria());
        
        // Conversión del ENUM a String para el DTO
        if (configuracion.getTipoVoz() != null) {
            dto.setTipoVoz(configuracion.getTipoVoz().name()); // Usamos .name() para obtener el nombre del enum como String
        } else {
            dto.setTipoVoz(null); // O un valor por defecto si lo prefieres
        }

        if (configuracion.getUsuario() != null) {
            dto.setUsuarioId(configuracion.getUsuario().getId());
        }
        return dto;
    }

    public Configuracion guardarConfiguracion(Configuracion configuracion) {
        return configuracionRepository.save(configuracion);
    }

    public void eliminarConfiguracion(Integer id) {
        configuracionRepository.deleteById(id);
    }
}