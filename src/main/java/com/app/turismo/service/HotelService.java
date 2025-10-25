package com.app.turismo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.turismo.model.HotelEntity;
import com.app.turismo.repository.HotelRepository;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    // Listar todos los hoteles
    public List<HotelEntity> listarHoteles() {
        return hotelRepository.findAll();
    }

    // Buscar hotel por ID
    public Optional<HotelEntity> buscarHotelPorId(Long id) {
        return hotelRepository.findById(id);
    }

    // Guardar nuevo hotel
    public HotelEntity guardarHotel(HotelEntity hotel) {
        return hotelRepository.save(hotel);
    }

    // Actualizar hotel existente
    public HotelEntity actualizarHotel(Long id, HotelEntity hotelActualizado) {
        return hotelRepository.findById(id)
                .map(hotel -> {
                    hotel.setNombre(hotelActualizado.getNombre());
                    hotel.setDireccion(hotelActualizado.getDireccion());
                    hotel.setTarifaAdulto(hotelActualizado.getTarifaAdulto());
                    hotel.setTarifaNino(hotelActualizado.getTarifaNino());
                    hotel.setAcomodacion(hotelActualizado.getAcomodacion());
                    hotel.setDestino(hotelActualizado.getDestino());
                    hotel.setDisponibilidad(hotelActualizado.getDisponibilidad());
                    return hotelRepository.save(hotel);
                })
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado con id: " + id));
    }

    // Eliminar hotel
    public void eliminarHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("No existe un hotel con el id: " + id);
        }
        hotelRepository.deleteById(id);
    }
}
