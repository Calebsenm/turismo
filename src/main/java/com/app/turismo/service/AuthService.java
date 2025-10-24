package com.app.turismo.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.turismo.config.Jwt.JwtService;
import com.app.turismo.dto.user.UserDto;
import com.app.turismo.model.UsuarioEntity;
import com.app.turismo.repository.UsuarioRepository;

import java.util.ArrayList;

@Service
public class AuthService implements UserDetailsService {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthService(UsuarioRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
    }

    public UserDto register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("El usuario con el email '" + userDto.getEmail() + "' ya existe.");
        }
        UsuarioEntity user = modelMapper.map(userDto, UsuarioEntity.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UsuarioEntity savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }


    public String login(String email, String password) {
        UsuarioEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        return jwtService.generateToken(user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + username));

        return new User(user.getEmail(), user.getPassword(), new ArrayList<>()); // Por ahora, sin roles/autoridades
    }
}
