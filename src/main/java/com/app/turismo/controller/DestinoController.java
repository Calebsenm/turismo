package com.app.turismo.controller;

import com.app.turismo.model.DestinoEntity;
import com.app.turismo.service.DestinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinos")
@CrossOrigin(origins = "*")
public class DestinoController {

    @Autowired
    private DestinoService destinoService;

    // GET - Listar todos los destinos
    @GetMapping
    public ResponseEntity<List<DestinoEntity>> listarDestinos() {
        return ResponseEntity.ok(destinoService.listarDestinos());
    }

    // GET - Obtener destino por ID
    @GetMapping("/{id}")
    public ResponseEntity<DestinoEntity> obtenerDestinoPorId(@PathVariable Long id) {
        return destinoService.obtenerDestinoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - Crear nuevo destino
    @PostMapping
    public ResponseEntity<DestinoEntity> crearDestino(@RequestBody DestinoEntity destino) {
        DestinoEntity nuevo = destinoService.crearDestino(destino);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // PUT - Actualizar destino existente
    @PutMapping("/{id}")
    public ResponseEntity<DestinoEntity> actualizarDestino(@PathVariable Long id, @RequestBody DestinoEntity detalles) {
        return destinoService.actualizarDestino(id, detalles)
                .map(actualizado -> new ResponseEntity<>(actualizado, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Eliminar destino
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDestino(@PathVariable Long id) {
        boolean eliminado = destinoService.eliminarDestino(id);
        if (eliminado)
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
