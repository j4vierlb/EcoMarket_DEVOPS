package com.grupo3.EcoMarket.service;

import com.grupo3.EcoMarket.model.Pedido;
import com.grupo3.EcoMarket.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    public Pedido crear(Pedido pedido) {
        pedido.setId(null);
        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDate.now());
        }
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizar(Long id, Pedido pedido) {
        Pedido existente = obtenerPorId(id);
        existente.setDescripcion(pedido.getDescripcion());
        existente.setFecha(pedido.getFecha() != null ? pedido.getFecha() : existente.getFecha());
        existente.setTotal(pedido.getTotal());
        return pedidoRepository.save(existente);
    }

    public void eliminar(Long id) {
        pedidoRepository.delete(obtenerPorId(id));
    }
}
