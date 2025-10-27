
package com.app.turismo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.turismo.model.PaqueteEntity;

@Repository
public interface PaqueteRepository extends JpaRepository<PaqueteEntity, Long> {
    // Consulta con fetch join para inicializar colecciones lazy
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p FROM PaqueteEntity p LEFT JOIN FETCH p.hoteles LEFT JOIN FETCH p.transportes LEFT JOIN FETCH p.actividades WHERE p.usuario.user_id = :id")
    java.util.List<PaqueteEntity> findByUsuarioIdWithAllRelations(
            @org.springframework.data.repository.query.Param("id") Long id);

    // Consulta para inicializar todas las relaciones (para admin)
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p FROM PaqueteEntity p LEFT JOIN FETCH p.hoteles LEFT JOIN FETCH p.transportes LEFT JOIN FETCH p.actividades")
    java.util.List<PaqueteEntity> findAllWithAllRelations();

}
