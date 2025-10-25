package com.app.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.turismo.model.HotelEntity;
import com.app.turismo.model.DestinoEntity;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long> {

    // 🔍 Buscar hoteles por destino
    List<HotelEntity> findByDestino(DestinoEntity destino);
}
