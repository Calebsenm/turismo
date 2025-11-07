
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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "*")
public class PaqueteController {
    // POST - Crear nuevo paquete
    @PostMapping
    public ResponseEntity<?> crearPaquete(
        @Valid
        @RequestBody PaqueteDTO paqueteDTO,
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        try {
            // Si el usuarioId no viene en el DTO, lo extraemos del JWT
            if (paqueteDTO.usuarioId == null && authHeader != null && authHeader.startsWith("Bearer ")) {
                String email = paqueteService.extraerEmailDesdeToken(authHeader);
                if (email != null) {
                    Optional<com.app.turismo.model.UsuarioEntity> usuarioOpt = paqueteService
                            .buscarUsuarioPorEmail(email);
                    usuarioOpt.ifPresent(u -> paqueteDTO.usuarioId = u.getUser_id());
                }
            }
            PaqueteEntity nuevo = paqueteService.crearPaqueteDesdeDTO(paqueteDTO);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear paquete: " + e.getMessage());
        }
    }

    // Endpoint para ver los paquetes del usuario autenticado
    @GetMapping("/mis")
    public ResponseEntity<List<PaqueteDTO>> listarMisPaquetes(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            email = paqueteService.extraerEmailDesdeToken(authHeader);
        }
        if (email == null) {
            // Si no hay token, devolver lista vacía (o puedes devolver UNAUTHORIZED si
            // prefieres)
            return ResponseEntity.ok(List.of());
        }
        List<PaqueteDTO> paquetes = paqueteService.listarPaquetesPorEmail(email);
        return ResponseEntity.ok(paquetes);
    }

    @Autowired
    private PaqueteService paqueteService;

    // 🧭 Listar todos los paquetes (DTO)
    @GetMapping
    public ResponseEntity<List<PaqueteDTO>> listarPaquetes() {
        return ResponseEntity.ok(paqueteService.listarPaquetes());
    }

    public ResponseEntity<PaqueteDTO> obtenerPaquetePorId(@PathVariable Long id) {
        Optional<PaqueteDTO> paquete = paqueteService.buscarPaquetePorId(id);
        return paquete.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para ver los paquetes de un usuario por su id (sin JWT)
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<PaqueteDTO>> listarPaquetesPorUsuario(@PathVariable Long id) {
        List<PaqueteDTO> paquetes = paqueteService.listarPaquetesPorUsuarioId(id);
        return ResponseEntity.ok(paquetes);
    }

    // ➕ Crear nuevo paquete (calcula costo automáticamente)
    // El método guardarPaquete no existe, se debe usar el método adecuado del
    // service
    // Si necesitas crear un paquete, usa el método correspondiente en
    // PaqueteService

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

    // Endpoint de descarga de PDF eliminado porque la funcionalidad no está
    // disponible sin itextpdf
}
