package com.app.turismo.controller;

import com.app.turismo.model.PaqueteEntity;
import com.app.turismo.service.PaqueteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "*")
public class PaqueteController {

    @Autowired
    private PaqueteService paqueteService;

    // Obtener todos los paquetes
    @GetMapping
    public ResponseEntity<List<PaqueteEntity>> listarPaquetes() {
        return ResponseEntity.ok(paqueteService.listarPaquetes());
    }

    // Obtener un paquete por ID
    @GetMapping("/{id}")
    public ResponseEntity<PaqueteEntity> obtenerPaquetePorId(@PathVariable Long id) {
        return paqueteService.obtenerPaquetePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear un nuevo paquete
    @PostMapping
    public ResponseEntity<PaqueteEntity> crearPaquete(@RequestBody PaqueteEntity paquete) {
        PaqueteEntity nuevoPaquete = paqueteService.crearPaquete(paquete);
        return new ResponseEntity<>(nuevoPaquete, HttpStatus.CREATED);
    }

    // Actualizar un paquete existente
    @PutMapping("/{id}")
    public ResponseEntity<PaqueteEntity> actualizarPaquete(@PathVariable Long id, @RequestBody PaqueteEntity detalles) {
        return paqueteService.actualizarPaquete(id, detalles)
                .map(actualizado -> new ResponseEntity<>(actualizado, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar paquete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaquete(@PathVariable Long id) {
        boolean eliminado = paqueteService.eliminarPaquete(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
