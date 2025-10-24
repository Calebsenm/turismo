package com.app.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.turismo.model.ActividadEntity;

@Repository
public interface  ActividadRepository extends JpaRepository<ActividadEntity, Long>{
    
}
