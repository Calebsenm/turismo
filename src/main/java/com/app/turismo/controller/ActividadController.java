package com.app.turismo.controller;

import com.app.turismo.model.ActividadEntity;
import com.app.turismo.service.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actividades")
@CrossOrigin(origins = "*")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;

    // GET - Listar todas las actividades
    @GetMapping
    public ResponseEntity<List<ActividadEntity>> listarActividades() {
        return ResponseEntity.ok(actividadService.listarActividades());
    }

    // GET - Buscar actividad por ID
    @GetMapping("/{id}")
    public ResponseEntity<ActividadEntity> obtenerActividadPorId(@PathVariable Long id) {
        return actividadService.obtenerActividadPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - Crear nueva actividad usando DTO
    @PostMapping
    public ResponseEntity<?> crearActividad(@RequestBody com.app.turismo.dto.ActividadDTO actividadDTO) {
        try {
            System.out.println(
                    "Datos recibidos: " + actividadDTO.getNombre() + ", destinoId: " + actividadDTO.getDestinoId());
            ActividadEntity nueva = actividadService.crearActividadDesdeDTO(actividadDTO);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error al crear actividad: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT - Actualizar actividad existente
    @PutMapping("/{id}")
    public ResponseEntity<ActividadEntity> actualizarActividad(@PathVariable Long id,
            @RequestBody ActividadEntity detalles) {
        return actividadService.actualizarActividad(id, detalles)
                .map(actualizada -> new ResponseEntity<>(actualizada, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Eliminar actividad
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarActividad(@PathVariable Long id) {
        boolean eliminado = actividadService.eliminarActividad(id);
        if (eliminado)
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
