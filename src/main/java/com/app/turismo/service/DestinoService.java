package com.app.turismo.service;

import com.app.turismo.model.DestinoEntity;
import com.app.turismo.repository.DestinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinoService {

    @Autowired
    private DestinoRepository destinoRepository;

    // Listar todos los destinos
    public List<DestinoEntity> listarDestinos() {
        return destinoRepository.findAll();
    }

    // Buscar destino por ID
    public Optional<DestinoEntity> obtenerDestinoPorId(Long id) {
        return destinoRepository.findById(id);
    }

    // Crear nuevo destino
    public DestinoEntity crearDestino(DestinoEntity destino) {
        return destinoRepository.save(destino);
    }

    // Actualizar destino existente
    public Optional<DestinoEntity> actualizarDestino(Long id, DestinoEntity detalles) {
        return destinoRepository.findById(id).map(destino -> {
            destino.setNombre(detalles.getNombre());
            destino.setDescripcion(detalles.getDescripcion());
            destino.setUbicacion(detalles.getUbicacion());
            return destinoRepository.save(destino);
        });
    }

    // Eliminar destino
    public boolean eliminarDestino(Long id) {
        if (destinoRepository.existsById(id)) {
            destinoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
