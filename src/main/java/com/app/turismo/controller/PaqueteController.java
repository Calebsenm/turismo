package com.app.turismo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.turismo.model.PaqueteEntity;
import com.app.turismo.service.PaqueteService;

@RestController
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "*")
public class PaqueteController {

    @Autowired
    private PaqueteService paqueteService;

    // 🧭 Listar todos los paquetes
    @GetMapping
    public ResponseEntity<List<PaqueteEntity>> listarPaquetes() {
        return ResponseEntity.ok(paqueteService.listarPaquetes());
    }

    // 🔎 Buscar paquete por ID
    @GetMapping("/{id}")
    public ResponseEntity<PaqueteEntity> obtenerPaquetePorId(@PathVariable Long id) {
        Optional<PaqueteEntity> paquete = paqueteService.buscarPaquetePorId(id);
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
