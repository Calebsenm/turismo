package com.app.turismo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.turismo.dto.HotelDTO;
import com.app.turismo.model.HotelEntity;
import com.app.turismo.service.HotelService;

import java.util.List;

@RestController
@RequestMapping("/api/hoteles")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // GET - Consultar todos los hoteles
    @GetMapping
    public List<HotelEntity> obtenerHoteles() {
        return hotelService.listarHoteles();
    }

    // GET - Consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<HotelEntity> obtenerHotelPorId(@PathVariable Long id) {
        return hotelService.buscarHotelPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - Registrar nuevo hotel usando DTO
    @PostMapping
    public ResponseEntity<?> crearHotel(@RequestBody HotelDTO hotelDTO) {
        try {
            System.out
                    .println("Datos recibidos: " + hotelDTO.getNombre() + ", destinoId: " + hotelDTO.getDestinoId());
            HotelEntity nuevoHotel = hotelService.crearHotelDesdeDTO(hotelDTO);
            return ResponseEntity.ok(nuevoHotel);
        } catch (Exception e) {
            System.err.println("Error al crear hotel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT - Modificar hotel existente
    @PutMapping("/{id}")
    public ResponseEntity<HotelEntity> actualizarHotel(@PathVariable Long id, @RequestBody HotelEntity hotel) {
        HotelEntity actualizado = hotelService.actualizarHotel(id, hotel);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE - Eliminar hotel
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHotel(@PathVariable Long id) {
        hotelService.eliminarHotel(id);
        return ResponseEntity.noContent().build();
    }
}
