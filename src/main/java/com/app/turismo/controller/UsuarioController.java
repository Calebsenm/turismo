package com.app.turismo.controller;

import com.app.turismo.model.UsuarioEntity;
import com.app.turismo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // GET - Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    // GET - Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Obtener usuario por email (útil para login)
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioEntity> obtenerUsuarioPorEmail(@PathVariable String email) {
        return usuarioService.obtenerUsuarioPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - Crear nuevo usuario
    @PostMapping
    public ResponseEntity<UsuarioEntity> crearUsuario(@RequestBody UsuarioEntity usuario) {
        UsuarioEntity nuevo = usuarioService.crearUsuario(usuario);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // PUT - Actualizar usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioEntity detalles) {
        return usuarioService.actualizarUsuario(id, detalles)
                .map(actualizado -> new ResponseEntity<>(actualizado, HttpStatus.OK))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        if (eliminado)
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof com.app.turismo.model.user.CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        com.app.turismo.model.user.CustomUserDetails userDetails = (com.app.turismo.model.user.CustomUserDetails) authentication
                .getPrincipal();
        UsuarioEntity usuario = userDetails.getUsuario();
        // Devolver solo los datos necesarios para el frontend
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {
            {
                put("email", usuario.getEmail());
                put("name", usuario.getName());
                put("userType", usuario.getUserType());
            }
        });
    }
}
