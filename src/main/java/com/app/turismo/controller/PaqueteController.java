package com.app.turismo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.turismo.model.PaqueteEntity;
import com.app.turismo.dto.PaqueteDTO;
import com.app.turismo.service.PaqueteService;

@RestController
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "*")
public class PaqueteController {

    @Autowired
    private PaqueteService paqueteService;

    // 🧭 Listar todos los paquetes (DTO)
    @GetMapping
    public ResponseEntity<List<PaqueteDTO>> listarPaquetes() {
        return ResponseEntity.ok(paqueteService.listarPaquetes());
    }

    // 🔎 Buscar paquete por ID (DTO)
    @GetMapping("/{id}")
    public ResponseEntity<PaqueteDTO> obtenerPaquetePorId(@PathVariable Long id) {
        Optional<PaqueteDTO> paquete = paqueteService.buscarPaquetePorId(id);
        return paquete.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ➕ Crear nuevo paquete (calcula costo automáticamente)
    @PostMapping
    public ResponseEntity<PaqueteEntity> crearPaquete(@RequestBody PaqueteEntity paquete) {
        PaqueteEntity nuevoPaquete = paqueteService.guardarPaquete(paquete);
        return new ResponseEntity<>(nuevoPaquete, HttpStatus.CREATED);
    }

    // 🔁 Actualizar paquete
    @PutMapping("/{id}")
    public ResponseEntity<PaqueteEntity> actualizarPaquete(@PathVariable Long id,
            @RequestBody PaqueteEntity paqueteActualizado) {
        try {
            PaqueteEntity actualizado = paqueteService.actualizarPaquete(id, paqueteActualizado);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ❌ Eliminar paquete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaquete(@PathVariable Long id) {
        try {
            paqueteService.eliminarPaquete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
