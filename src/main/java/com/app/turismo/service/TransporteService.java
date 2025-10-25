package com.app.turismo.service;

import com.app.turismo.model.TransporteEntity;
import com.app.turismo.repository.TransporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransporteService {

    @Autowired
    private TransporteRepository transporteRepository;

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

    // Actualizar transporte existente
    public Optional<TransporteEntity> actualizarTransporte(Long id, TransporteEntity transporteDetalles) {
        return transporteRepository.findById(id).map(transporte -> {
            transporte.setTipo(transporteDetalles.getTipo());
            transporte.setEmpresa(transporteDetalles.getEmpresa());
            transporte.setPrecio(transporteDetalles.getPrecio());
            transporte.setDestino(transporteDetalles.getDestino());
            return transporteRepository.save(transporte);
        });
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
