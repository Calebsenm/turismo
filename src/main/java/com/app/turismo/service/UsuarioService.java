package com.app.turismo.service;

import com.app.turismo.model.UsuarioEntity;
import com.app.turismo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar todos los usuarios
    public List<UsuarioEntity> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<UsuarioEntity> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Buscar usuario por email
    public Optional<UsuarioEntity> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Crear usuario
    public UsuarioEntity crearUsuario(UsuarioEntity usuario) {
        // Aquí podrías agregar validaciones o encriptar la contraseña
        return usuarioRepository.save(usuario);
    }

    // Actualizar usuario existente
    public Optional<UsuarioEntity> actualizarUsuario(Long id, UsuarioEntity detalles) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setEmail(detalles.getEmail());
            usuario.setPassword(detalles.getPassword());
            usuario.setName(detalles.getName());
            usuario.setUserType(detalles.getUserType());
            return usuarioRepository.save(usuario);
        });
    }

    // Eliminar usuario
    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
