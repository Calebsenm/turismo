package com.app.turismo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.turismo.model.PaqueteEntity;

@Repository
public interface PaqueteRepository extends JpaRepository<PaqueteEntity, Long>{
    
}
