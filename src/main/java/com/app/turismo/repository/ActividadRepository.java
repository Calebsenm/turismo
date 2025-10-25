package com.app.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.turismo.model.ActividadEntity;
import com.app.turismo.model.DestinoEntity;
import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<ActividadEntity, Long> {

    // 🔍 Buscar actividades por destino
    List<ActividadEntity> findByDestino(DestinoEntity destino);
}
