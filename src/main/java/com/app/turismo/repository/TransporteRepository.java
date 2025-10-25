package com.app.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.turismo.model.TransporteEntity;
import com.app.turismo.model.DestinoEntity;
import java.util.List;

@Repository
public interface TransporteRepository extends JpaRepository<TransporteEntity, Long> {

    // 🔍 Buscar transportes por destino
    List<TransporteEntity> findByDestino(DestinoEntity destino);
}
