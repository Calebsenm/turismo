package com.app.turismo.service;

import com.app.turismo.model.DestinoEntity;
import com.app.turismo.repository.DestinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.app.turismo.dto.DestinoDTO;
import com.app.turismo.dto.ActividadDTO;
import com.app.turismo.dto.HotelDTO;
import com.app.turismo.dto.TransporteDTO;

@Service
public class DestinoService {

    @Autowired
    private DestinoRepository destinoRepository;

    // Listar todos los destinos como DTO
    @Transactional
    public List<DestinoDTO> listarDestinos() {
        List<DestinoEntity> destinos = destinoRepository.findAll();
        return destinos.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Buscar destino por ID y retornar DTO
    @Transactional
    public Optional<DestinoDTO> obtenerDestinoPorId(Long id) {
        return destinoRepository.findById(id)
                .map(this::mapToDTO);
    }

    // Método para mapear DestinoEntity a DestinoDTO
    private DestinoDTO mapToDTO(DestinoEntity entity) {
        DestinoDTO dto = new DestinoDTO();
        dto.setDestino_id(entity.getDestino_id());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setUbicacion(entity.getUbicacion());
        // Mapear actividades
        dto.setActividades(entity.getActividades() != null ? entity.getActividades().stream().map(act -> {
            ActividadDTO a = new ActividadDTO();
            a.setActividad_id(act.getActividad_id());
            a.setNombre(act.getNombre());
            a.setDescripcion(act.getDescripcion());
            a.setPrecio(act.getPrecio());
            return a;
        }).collect(Collectors.toList()) : new java.util.ArrayList<>());

        // Mapear hoteles
        dto.setHoteles(entity.getHoteles() != null ? entity.getHoteles().stream().map(hotel -> {
            HotelDTO h = new HotelDTO();
            h.setHotel_id(hotel.getHotel_id());
            h.setNombre(hotel.getNombre());
            h.setTarifaAdulto(hotel.getTarifaAdulto());
            h.setTarifaNino(hotel.getTarifaNino());
            return h;
        }).collect(Collectors.toList()) : new java.util.ArrayList<>());

        // Mapear transportes
        dto.setTransportes(entity.getTransportes() != null ? entity.getTransportes().stream().map(transporte -> {
            TransporteDTO t = new TransporteDTO();
            t.setTransporte_id(transporte.getTransporte_id());
            t.setEmpresa(transporte.getEmpresa());
            t.setTipo(transporte.getTipo());
            t.setPrecio(transporte.getPrecio());
            return t;
        }).collect(Collectors.toList()) : new java.util.ArrayList<>());
        // No incluir paquetes para evitar recursión
        return dto;
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
