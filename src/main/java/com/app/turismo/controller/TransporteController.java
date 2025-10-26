package com.app.turismo.controller;

import com.app.turismo.model.TransporteEntity;
import com.app.turismo.service.TransporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportes")
@CrossOrigin(origins = "*")
public class TransporteController {

    @Autowired
    private TransporteService transporteService;

    // GET - Listar todos los transportes
    @GetMapping
    public ResponseEntity<List<TransporteEntity>> listarTransportes() {
        return ResponseEntity.ok(transporteService.listarTransportes());
    }

    // GET - Obtener transporte por ID
    @GetMapping("/{id}")
    public ResponseEntity<TransporteEntity> obtenerTransportePorId(@PathVariable Long id) {
        return transporteService.obtenerTransportePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - Crear transporte usando DTO
    @PostMapping
    public ResponseEntity<?> crearTransporte(
            @RequestBody com.app.turismo.dto.TransporteDTO transporteDTO) {
        try {
            System.out.println(
                    "Datos recibidos: " + transporteDTO.getTipo() + ", destinoId: " + transporteDTO.getDestinoId());
            TransporteEntity nuevoTransporte = transporteService.crearTransporteDesdeDTO(transporteDTO);
            return new ResponseEntity<>(nuevoTransporte, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error al crear transporte: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT - Actualizar transporte existente
    @PutMapping("/{id}")
    public ResponseEntity<TransporteEntity> actualizarTransporte(@PathVariable Long id,
            @RequestBody com.app.turismo.dto.TransporteDTO transporteDTO) {
        try {
            TransporteEntity actualizado = transporteService.actualizarTransporteDesdeDTO(id, transporteDTO);
            if (actualizado != null) {
                return new ResponseEntity<>(actualizado, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE - Eliminar transporte
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransporte(@PathVariable Long id) {
        boolean eliminado = transporteService.eliminarTransporte(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
