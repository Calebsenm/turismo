package com.app.turismo.service;

import com.app.turismo.dto.ActividadDTO;
import com.app.turismo.model.ActividadEntity;
import com.app.turismo.model.DestinoEntity;
import com.app.turismo.repository.ActividadRepository;
import com.app.turismo.repository.DestinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private DestinoRepository destinoRepository;

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

    // Crear actividad desde DTO (convierte destino_id a DestinoEntity)
    public ActividadEntity crearActividadDesdeDTO(ActividadDTO actividadDTO) {
        // Buscar el destino por ID
        DestinoEntity destino = destinoRepository.findById(actividadDTO.getDestinoId())
                .orElseThrow(
                        () -> new RuntimeException("Destino no encontrado con id: " + actividadDTO.getDestinoId()));

        // Crear la entidad Actividad
        ActividadEntity actividad = new ActividadEntity();
        actividad.setNombre(actividadDTO.getNombre());
        actividad.setDescripcion(actividadDTO.getDescripcion());
        actividad.setPrecio(actividadDTO.getPrecio());
        actividad.setDestino(destino);

        return actividadRepository.save(actividad);
    }

    // Actualizar actividad desde DTO
    public ActividadEntity actualizarActividadDesdeDTO(Long id, ActividadDTO actividadDTO) {
        ActividadEntity actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada con id: " + id));

        actividad.setNombre(actividadDTO.getNombre());
        actividad.setDescripcion(actividadDTO.getDescripcion());
        actividad.setPrecio(actividadDTO.getPrecio());

        if (actividadDTO.getDestinoId() == null) {
            throw new RuntimeException("destinoId no puede ser null");
        }
        DestinoEntity destino = destinoRepository.findById(actividadDTO.getDestinoId())
                .orElseThrow(
                        () -> new RuntimeException("Destino no encontrado con id: " + actividadDTO.getDestinoId()));
        actividad.setDestino(destino);

        return actividadRepository.save(actividad);
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
