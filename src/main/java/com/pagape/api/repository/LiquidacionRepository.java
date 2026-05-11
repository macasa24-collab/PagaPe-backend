package com.pagape.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.Liquidacion;

@Repository
public interface LiquidacionRepository extends JpaRepository<Liquidacion, Integer> {
}
