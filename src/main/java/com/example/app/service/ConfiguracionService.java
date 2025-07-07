package com.example.app.service;

import com.example.app.model.Configuracion;
import com.example.app.repository.ConfiguracionRepository;
import com.example.app.dto.ConfiguracionSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfiguracionService {

    @Autowired
    private ConfiguracionRepository configuracionRepository;

    public Optional<ConfiguracionSimple> getConfiguracionSimpleByUsuarioId(Long usuarioId) {
        Optional<Configuracion> configuracionOptional = Optional.ofNullable(configuracionRepository.buscarPorUsuario(usuarioId));

        if (configuracionOptional.isPresent()) {
            return configuracionOptional.map(this::convertToSimpleDto);
        } else {
            return Optional.empty();
        }
    }

    private ConfiguracionSimple convertToSimpleDto(Configuracion configuracion) {
        ConfiguracionSimple dto = new ConfiguracionSimple();
        dto.setId(configuracion.getId());
        dto.setBotonesPorPantalla(configuracion.getBotonesPorPantalla());
        dto.setMostrarPorCategoria(configuracion.getMostrarPorCategoria());
        
        if (configuracion.getTipoVoz() != null) {
            dto.setTipoVoz(configuracion.getTipoVoz().name()); 
        } else {
            dto.setTipoVoz(null); 
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