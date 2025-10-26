package com.app.turismo.service;

import com.app.turismo.dto.TransporteDTO;
import com.app.turismo.model.DestinoEntity;
import com.app.turismo.model.TransporteEntity;
import com.app.turismo.repository.DestinoRepository;
import com.app.turismo.repository.TransporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransporteService {

    @Autowired
    private TransporteRepository transporteRepository;

    @Autowired
    private DestinoRepository destinoRepository;

    // Listar todos los transportes
    public List<TransporteEntity> listarTransportes() {
        return transporteRepository.findAll();
    }

    // Buscar transporte por ID
    public Optional<TransporteEntity> obtenerTransportePorId(Long id) {
        return transporteRepository.findById(id);
    }

    // Crear transporte
    public TransporteEntity crearTransporte(TransporteEntity transporte) {
        return transporteRepository.save(transporte);
    }

    // Crear transporte desde DTO (convierte destino_id a DestinoEntity)
    public TransporteEntity crearTransporteDesdeDTO(TransporteDTO transporteDTO) {
        // Buscar el destino por ID
        DestinoEntity destino = destinoRepository.findById(transporteDTO.getDestinoId())
                .orElseThrow(
                        () -> new RuntimeException("Destino no encontrado con id: " + transporteDTO.getDestinoId()));

        // Crear la entidad Transporte
        TransporteEntity transporte = new TransporteEntity();
        transporte.setTipo(transporteDTO.getTipo());
        transporte.setEmpresa(transporteDTO.getEmpresa());
        transporte.setPrecio(transporteDTO.getPrecio());
        transporte.setDestino(destino);

        return transporteRepository.save(transporte);
    }

    // Actualizar transporte existente
    public TransporteEntity actualizarTransporteDesdeDTO(Long id, com.app.turismo.dto.TransporteDTO transporteDTO) {
        Optional<TransporteEntity> optTransporte = transporteRepository.findById(id);
        if (optTransporte.isPresent()) {
            TransporteEntity transporte = optTransporte.get();
            transporte.setTipo(transporteDTO.getTipo());
            transporte.setEmpresa(transporteDTO.getEmpresa());
            transporte.setPrecio(transporteDTO.getPrecio());
            // Buscar el destino por id y asignar
            DestinoEntity destino = destinoRepository.findById(transporteDTO.getDestinoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Destino no encontrado con id: " + transporteDTO.getDestinoId()));
            transporte.setDestino(destino);
            return transporteRepository.save(transporte);
        }
        return null;
    }

    // Eliminar transporte
    public boolean eliminarTransporte(Long id) {
        if (transporteRepository.existsById(id)) {
            transporteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
