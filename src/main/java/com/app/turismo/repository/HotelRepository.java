package com.app.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.turismo.model.HotelEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long>{
    
}
