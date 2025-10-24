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

    // POST - Crear transporte
    @PostMapping
    public ResponseEntity<TransporteEntity> crearTransporte(@RequestBody TransporteEntity transporte) {
        TransporteEntity nuevoTransporte = transporteService.crearTransporte(transporte);
        return new ResponseEntity<>(nuevoTransporte, HttpStatus.CREATED);
    }

    // PUT - Actualizar transporte existente
    @PutMapping("/{id}")
    public ResponseEntity<TransporteEntity> actualizarTransporte(@PathVariable Long id,
            @RequestBody TransporteEntity transporteDetalles) {
        return transporteService.actualizarTransporte(id, transporteDetalles)
                .map(actualizado -> new ResponseEntity<>(actualizado, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
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
