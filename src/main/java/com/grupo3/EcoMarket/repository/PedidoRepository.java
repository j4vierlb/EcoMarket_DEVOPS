package com.grupo3.EcoMarket.repository;

import com.grupo3.EcoMarket.model.pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<pedido, Long> {
}