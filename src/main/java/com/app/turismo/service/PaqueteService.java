package com.app.turismo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.turismo.model.*;
import com.app.turismo.repository.*;
// import com.itextpdf.kernel.geom.PageSize;
// import com.itextpdf.kernel.pdf.PdfDocument;
// import com.itextpdf.kernel.pdf.PdfWriter;
// import com.itextpdf.layout.Document;
// import com.itextpdf.layout.element.Paragraph;
// import com.itextpdf.layout.properties.TextAlignment;
import com.app.turismo.dto.PaqueteDTO;
import java.util.stream.Collectors;

@Service
public class PaqueteService {
    // Devuelve los paquetes de un usuario por su id
    public List<PaqueteDTO> listarPaquetesPorUsuarioId(Long id) {
        if (id == null)
            return List.of();
        // Usar un método personalizado en el repositorio para traer los paquetes con
        // hoteles, transportes y actividades inicializados
        List<PaqueteEntity> paquetes = paqueteRepository.findByUsuarioIdWithAllRelations(id);
        return paquetes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Permite buscar usuario por email desde el controlador
    public Optional<UsuarioEntity> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Crear paquete desde DTO
    public PaqueteEntity crearPaqueteDesdeDTO(PaqueteDTO dto) {
        PaqueteEntity paquete = new PaqueteEntity();
        // Asignar usuario
        if (dto.usuarioId != null) {
            Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(dto.usuarioId);
            usuarioOpt.ifPresent(paquete::setUsuario);
        }
        // Asignar destino
        if (dto.destinoId != null) {
            Optional<DestinoEntity> destinoOpt = destinoRepository.findById(dto.destinoId);
            destinoOpt.ifPresent(paquete::setDestino);
        }
        paquete.setOrigen(dto.origen);
        paquete.setFechaInicio(dto.fechaInicio);
        paquete.setFechaFin(dto.fechaFin);
        paquete.setNombre(dto.nombre);
        paquete.setDescripcion(dto.descripcion);
        paquete.setNumAdultos(dto.numAdultos);
        paquete.setNumNinos(dto.numNinos);
        paquete.setTipoPaquete(dto.tipoPaquete);
        // Asignar hoteles
        if (dto.hoteles != null) {
            paquete.setHoteles(new java.util.HashSet<>(dto.hoteles));
        }
        // Asignar transportes
        if (dto.transportes != null) {
            paquete.setTransportes(new java.util.HashSet<>(dto.transportes));
        }
        // Asignar actividades
        if (dto.actividades != null) {
            paquete.setActividades(new java.util.HashSet<>(dto.actividades));
        }
        // Calcular costo total (puedes mejorar la lógica)
        paquete.setCostoTotal(dto.costoTotal != null ? dto.costoTotal : 0.0);
        return paqueteRepository.save(paquete);
    }

    // Extrae el email del token JWT (simplificado, depende de tu implementación
    // real)
    // Requiere la dependencia jjwt en tu pom.xml
    // <dependency>
    // <groupId>io.jsonwebtoken</groupId>
    // <artifactId>jjwt</artifactId>
    // <version>0.9.1</version>
    // </dependency>
    @org.springframework.beans.factory.annotation.Value("${application.security.jwt.secret-key}")
    private String jwtSecretKey;

    public String extraerEmailDesdeToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;
        String token = authHeader.substring(7);
        try {
            io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser()
                    .setSigningKey(jwtSecretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // El email suele estar en el subject
        } catch (Exception e) {
            return null;
        }
    }

    // Devuelve los paquetes de un usuario por email
    public List<PaqueteDTO> listarPaquetesPorEmail(String email) {
        if (email == null)
            return List.of();
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty())
            return List.of();
        UsuarioEntity usuario = usuarioOpt.get();
        List<PaqueteEntity> paquetes = paqueteRepository.findAll().stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().getUser_id().equals(usuario.getUser_id()))
                .toList();
        // Inicializar manualmente transportes y actividades para cada paquete
        for (PaqueteEntity p : paquetes) {
            try {
                org.hibernate.Hibernate.initialize(p.getTransportes());
            } catch (Exception e) {
            }
            try {
                org.hibernate.Hibernate.initialize(p.getActividades());
            } catch (Exception e) {
            }
        }
        return paquetes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Método de mapeo de entidad a DTO
    private PaqueteDTO mapToDTO(PaqueteEntity entity) {
        PaqueteDTO dto = new PaqueteDTO();
        dto.paqueteId = entity.getPaquete_id();
        dto.usuarioId = entity.getUsuario() != null ? entity.getUsuario().getUser_id() : null;
        dto.destinoId = entity.getDestino() != null ? entity.getDestino().getDestino_id() : null;
        dto.origen = entity.getOrigen();
        dto.fechaInicio = entity.getFechaInicio();
        dto.fechaFin = entity.getFechaFin();
        dto.costoTotal = entity.getCostoTotal();
        dto.nombre = entity.getNombre();
        dto.descripcion = entity.getDescripcion();
        dto.numAdultos = entity.getNumAdultos();
        dto.numNinos = entity.getNumNinos();
        dto.tipoPaquete = entity.getTipoPaquete();
        // Convertir listas de entidades a listas de IDs
        // Inicializar manualmente transportes y actividades para evitar
        // LazyInitializationException
        try {
            org.hibernate.Hibernate.initialize(entity.getTransportes());
        } catch (Exception e) {
        }
        try {
            org.hibernate.Hibernate.initialize(entity.getActividades());
        } catch (Exception e) {
        }
        // Enviar objetos completos para frontend admin
        dto.hoteles = entity.getHoteles() != null ? entity.getHoteles().stream().collect(Collectors.toList()) : null;
        dto.transportes = entity.getTransportes() != null
                ? entity.getTransportes().stream().collect(Collectors.toList())
                : null;
        dto.actividades = entity.getActividades() != null
                ? entity.getActividades().stream().collect(Collectors.toList())
                : null;
        return dto;
    }

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private TransporteRepository transporteRepository;

    @Autowired
    private DestinoRepository destinoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Lista todos los paquetes registrados
     */
    @Transactional(readOnly = true)
    public List<PaqueteDTO> listarPaquetes() {
        // Usar fetch join para inicializar todas las relaciones
        return paqueteRepository.findAllWithAllRelations().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un paquete por su ID
     */
    @Transactional(readOnly = true)
    public Optional<PaqueteDTO> buscarPaquetePorId(Long id) {
        return paqueteRepository.findById(id).map(this::mapToDTO);
    }

    /**
     * Determina el tipo de transporte más adecuado según el origen y el destino.
     */
    private String determinarTipoTransporte(String origen, String destinoUbicacion) {
        if (origen == null || destinoUbicacion == null)
            return "terrestre";

        origen = origen.toLowerCase();
        destinoUbicacion = destinoUbicacion.toLowerCase();

        // 🔸 Si están en el mismo departamento o región
        if (origen.contains("córdoba") && destinoUbicacion.contains("córdoba"))
            return "terrestre";
        if (origen.contains("sucre") && destinoUbicacion.contains("sucre"))
            return "terrestre";
        if (origen.contains("antioquia") && destinoUbicacion.contains("antioquia"))
            return "terrestre";

        // 🔸 Si el destino es costero y el origen no
        if ((destinoUbicacion.contains("coveñas") || destinoUbicacion.contains("cartagena")
                || destinoUbicacion.contains("capurganá"))
                && !origen.contains("costa")) {
            return "marítimo";
        }

        // 🔸 Por defecto si están lejos → aéreo
        return "aéreo";
    }

    /**
     * Define costos base por tipo de transporte (si no hay datos en DB)
     */
    private double calcularCostoBase(String tipo) {
        switch (tipo.toLowerCase()) {
            case "aéreo":
                return 400000;
            case "marítimo":
                return 180000;
            default:
                return 90000;
        }
    }

    /**
     * Actualiza un paquete existente
     */
    public PaqueteEntity actualizarPaquete(Long id, PaqueteEntity paqueteActualizado) {
        return paqueteRepository.findById(id)
                .map(paquete -> {
                    paquete.setNombre(paqueteActualizado.getNombre());
                    paquete.setDescripcion(paqueteActualizado.getDescripcion());
                    paquete.setFechaInicio(paqueteActualizado.getFechaInicio());
                    paquete.setFechaFin(paqueteActualizado.getFechaFin());
                    paquete.setDestino(paqueteActualizado.getDestino());
                    paquete.setOrigen(paqueteActualizado.getOrigen());
                    paquete.setNumAdultos(paqueteActualizado.getNumAdultos());
                    paquete.setNumNinos(paqueteActualizado.getNumNinos());
                    // Si necesitas recalcular el costo, agrega la lógica aquí
                    return paqueteRepository.save(paquete);
                })
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado con id: " + id));
    }

    /**
     * Elimina un paquete por ID
     */
    public void eliminarPaquete(Long id) {
        if (!paqueteRepository.existsById(id)) {
            throw new RuntimeException("No existe un paquete con el id: " + id);
        }
        paqueteRepository.deleteById(id);
    }

    /**
     * Obtiene un paquete con todas sus relaciones cargadas (para generación de PDF)
     * 
     * @param id ID del paquete
     * @return Optional con el paquete y todas sus relaciones inicializadas
     */
    @Transactional(readOnly = true)
    public Optional<PaqueteEntity> obtenerPaqueteConRelaciones(Long id) {
        Optional<PaqueteEntity> paqueteOpt = paqueteRepository.findById(id);
        
        if (paqueteOpt.isPresent()) {
            PaqueteEntity paquete = paqueteOpt.get();
            // Inicializar todas las colecciones lazy
            org.hibernate.Hibernate.initialize(paquete.getHoteles());
            org.hibernate.Hibernate.initialize(paquete.getTransportes());
            org.hibernate.Hibernate.initialize(paquete.getActividades());
            org.hibernate.Hibernate.initialize(paquete.getDestino());
            org.hibernate.Hibernate.initialize(paquete.getUsuario());
        }
        
        return paqueteOpt;
    }

    /**
     * Valida que un usuario tenga permisos para acceder a un paquete específico.
     * Un usuario puede acceder si es el propietario del paquete o si es administrador.
     * 
     * @param paqueteId ID del paquete
     * @param userEmail Email del usuario
     * @return true si el usuario tiene acceso, false en caso contrario
     */
    public boolean validarPermisoUsuario(Long paqueteId, String userEmail) {
        // Buscar el paquete
        Optional<PaqueteEntity> paqueteOpt = paqueteRepository.findById(paqueteId);
        if (paqueteOpt.isEmpty()) {
            return false;
        }
        
        PaqueteEntity paquete = paqueteOpt.get();
        
        // Buscar el usuario
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByEmail(userEmail);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        
        UsuarioEntity usuario = usuarioOpt.get();
        
        // Verificar si el usuario es el propietario del paquete o es administrador
        boolean esPropietario = paquete.getUsuario() != null 
                && paquete.getUsuario().getUser_id().equals(usuario.getUser_id());
        boolean esAdmin = "admin".equalsIgnoreCase(usuario.getUserType());
        
        return esPropietario || esAdmin;
    }

}
