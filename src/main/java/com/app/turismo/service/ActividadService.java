package com.app.turismo.service;

import com.app.turismo.model.ActividadEntity;
import com.app.turismo.repository.ActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    // Listar todas las actividades
    public List<ActividadEntity> listarActividades() {
        return actividadRepository.findAll();
    }

    // Buscar actividad por ID
    public Optional<ActividadEntity> obtenerActividadPorId(Long id) {
        return actividadRepository.findById(id);
    }

    // Crear actividad
    public ActividadEntity crearActividad(ActividadEntity actividad) {
        return actividadRepository.save(actividad);
    }

    // Actualizar actividad
    public Optional<ActividadEntity> actualizarActividad(Long id, ActividadEntity detalles) {
        return actividadRepository.findById(id).map(actividad -> {
            actividad.setNombre(detalles.getNombre());
            actividad.setDescripcion(detalles.getDescripcion());
            actividad.setPrecio(detalles.getPrecio());
            actividad.setDestino(detalles.getDestino());
            return actividadRepository.save(actividad);
        });
    }

    // Eliminar actividad
    public boolean eliminarActividad(Long id) {
        if (actividadRepository.existsById(id)) {
            actividadRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
