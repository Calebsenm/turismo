package com.app.turismo.service;

import com.app.turismo.model.PaqueteEntity;
import com.app.turismo.repository.PaqueteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaqueteService {

    @Autowired
    private PaqueteRepository paqueteRepository;

    // Listar todos los paquetes
    public List<PaqueteEntity> listarPaquetes() {
        return paqueteRepository.findAll();
    }

    // Buscar un paquete por ID
    public Optional<PaqueteEntity> obtenerPaquetePorId(Long id) {
        return paqueteRepository.findById(id);
    }

    // Crear un paquete nuevo
    public PaqueteEntity crearPaquete(PaqueteEntity paquete) {
        // Aquí puedes agregar lógica de negocio, por ejemplo:
        // calcular costo total automáticamente según hoteles, actividades, transporte,
        // etc.
        return paqueteRepository.save(paquete);
    }

    // Actualizar un paquete existente
    public Optional<PaqueteEntity> actualizarPaquete(Long id, PaqueteEntity paqueteDetalles) {
        return paqueteRepository.findById(id).map(paquete -> {
            paquete.setUsuario(paqueteDetalles.getUsuario());
            paquete.setDestino(paqueteDetalles.getDestino());
            paquete.setFechaInicio(paqueteDetalles.getFechaInicio());
            paquete.setFechaFin(paqueteDetalles.getFechaFin());
            paquete.setCostoTotal(paqueteDetalles.getCostoTotal());
            paquete.setNombre(paqueteDetalles.getNombre());
            paquete.setDescripcion(paqueteDetalles.getDescripcion());
            return paqueteRepository.save(paquete);
        });
    }

    // Eliminar paquete
    public boolean eliminarPaquete(Long id) {
        if (paqueteRepository.existsById(id)) {
            paqueteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
