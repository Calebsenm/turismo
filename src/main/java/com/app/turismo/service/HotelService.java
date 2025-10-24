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

    public List<HotelEntity> listarHoteles() {
        return hotelRepository.findAll();
    }

    public Optional<HotelEntity> buscarHotelPorId(Long id) {
        return hotelRepository.findById(id);
    }

    public HotelEntity guardarHotel(HotelEntity hotel) {
        return hotelRepository.save(hotel);
    }

    public HotelEntity actualizarHotel(Long id, HotelEntity hotelActualizado) {
        return hotelRepository.findById(id)
                .map(hotel -> {
                    hotel.setNombre(hotelActualizado.getNombre());
                    hotel.setDireccion(hotelActualizado.getDireccion());
                    hotel.setPrice(hotelActualizado.getPrice());
                    hotel.setAcomodacion(hotelActualizado.getAcomodacion());
                    hotel.setDestino(hotelActualizado.getDestino());
                    return hotelRepository.save(hotel);
                })
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado con id: " + id));
    }

    public void eliminarHotel(Long id) {
        hotelRepository.deleteById(id);
    }
}
