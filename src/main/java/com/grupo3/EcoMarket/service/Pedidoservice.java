package com.grupo3.EcoMarket.service;

import com.grupo3.EcoMarket.model.pedido;
import com.grupo3.EcoMarket.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Pedidoservice {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public pedido guardar(pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }
}
