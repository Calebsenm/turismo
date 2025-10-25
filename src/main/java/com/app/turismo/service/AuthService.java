package com.app.turismo.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.turismo.config.Jwt.JwtService;
import com.app.turismo.dto.user.LoginRequest;
import com.app.turismo.dto.user.UserDto;
import com.app.turismo.exception.InvalidCredentialsException;
import com.app.turismo.exception.UserAlreadyExistsException;
import com.app.turismo.model.UsuarioEntity;
import com.app.turismo.repository.UsuarioRepository;


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
            throw new UserAlreadyExistsException("El usuario con el email '" + userDto.getEmail() + "' ya existe.");
        }
        UsuarioEntity user = modelMapper.map(userDto, UsuarioEntity.class);
        if (user.getUserType() == null) {
            user.setUserType("CLIENTE"); 
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UsuarioEntity savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }


    public String login(LoginRequest loginRequest) {
        UsuarioEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email o contraseña incorrectos."));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos.");
        }
        return jwtService.generateToken(user); 
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + username));
    }
}
