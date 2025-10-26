package com.app.turismo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.turismo.dto.HotelDTO;
import com.app.turismo.model.DestinoEntity;
import com.app.turismo.model.HotelEntity;
import com.app.turismo.repository.DestinoRepository;
import com.app.turismo.repository.HotelRepository;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private DestinoRepository destinoRepository;

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

    // Crear hotel desde DTO (convierte destino_id a DestinoEntity)
    public HotelEntity crearHotelDesdeDTO(HotelDTO hotelDTO) {
        System.out.println("=== HotelService.crearHotelDesdeDTO ===");
        System.out.println("DTO recibido: nombre=" + hotelDTO.getNombre() + ", destinoId=" + hotelDTO.getDestinoId());

        if (hotelDTO.getDestinoId() == null) {
            throw new RuntimeException("destinoId no puede ser null");
        }

        // Buscar el destino por ID
        System.out.println("Buscando destino con ID: " + hotelDTO.getDestinoId());
        DestinoEntity destino = destinoRepository.findById(hotelDTO.getDestinoId())
                .orElseThrow(() -> new RuntimeException("Destino no encontrado con id: " + hotelDTO.getDestinoId()));

        System.out.println("Destino encontrado: " + destino.getNombre());

        // Crear la entidad Hotel
        HotelEntity hotel = new HotelEntity();
        hotel.setNombre(hotelDTO.getNombre());
        hotel.setTarifaAdulto(hotelDTO.getTarifaAdulto());
        hotel.setTarifaNino(hotelDTO.getTarifaNino());
        hotel.setDestino(destino);

        System.out.println("Guardando hotel...");
        HotelEntity hotelGuardado = hotelRepository.save(hotel);
        System.out.println("Hotel guardado con ID: " + hotelGuardado.getHotel_id());

        return hotelGuardado;
    }

    // Actualizar hotel existente desde DTO
    public HotelEntity actualizarHotelDesdeDTO(Long id, HotelDTO hotelDTO) {
        HotelEntity hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado con id: " + id));

        hotel.setNombre(hotelDTO.getNombre());
        hotel.setTarifaAdulto(hotelDTO.getTarifaAdulto());
        hotel.setTarifaNino(hotelDTO.getTarifaNino());

        if (hotelDTO.getDestinoId() == null) {
            throw new RuntimeException("destinoId no puede ser null");
        }
        DestinoEntity destino = destinoRepository.findById(hotelDTO.getDestinoId())
                .orElseThrow(() -> new RuntimeException("Destino no encontrado con id: " + hotelDTO.getDestinoId()));
        hotel.setDestino(destino);

        return hotelRepository.save(hotel);
    }

    // Eliminar hotel
    public void eliminarHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("No existe un hotel con el id: " + id);
        }
        hotelRepository.deleteById(id);
    }

}
