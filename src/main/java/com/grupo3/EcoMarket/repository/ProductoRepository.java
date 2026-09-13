package com.grupo3.EcoMarket.repository;

import com.grupo3.EcoMarket.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}