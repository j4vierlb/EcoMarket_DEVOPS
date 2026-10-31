package com.grupo3.EcoMarket.service;

import com.grupo3.EcoMarket.model.Pedido;
import com.grupo3.EcoMarket.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido(Long id, LocalDate fecha) {
        Pedido p = new Pedido();
        p.setId(id);
        p.setDescripcion("Canasta semanal");
        p.setFecha(fecha);
        p.setTotal(new BigDecimal("25990"));
        return p;
    }

    @Test
    void obtenerTodosDevuelveLosPedidosDelRepositorio() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido(1L, LocalDate.now())));

        assertThat(pedidoService.obtenerTodos()).hasSize(1);
    }

    @Test
    void crearSinFechaUsaLaFechaActualEIgnoraElId() {
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido creado = pedidoService.crear(pedido(7L, null));

        assertThat(creado.getId()).isNull();
        assertThat(creado.getFecha()).isEqualTo(LocalDate.now());
    }

    @Test
    void crearConFechaLaRespeta() {
        LocalDate fecha = LocalDate.of(2026, 1, 15);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(pedidoService.crear(pedido(null, fecha)).getFecha()).isEqualTo(fecha);
    }

    @Test
    void actualizarSinFechaConservaLaFechaExistente() {
        LocalDate original = LocalDate.of(2026, 1, 15);
        Pedido existente = pedido(1L, original);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoRepository.save(existente)).thenReturn(existente);

        Pedido cambios = pedido(null, null);
        cambios.setDescripcion("Canasta quincenal");
        cambios.setTotal(new BigDecimal("49990"));
        Pedido actualizado = pedidoService.actualizar(1L, cambios);

        assertThat(actualizado.getFecha()).isEqualTo(original);
        assertThat(actualizado.getDescripcion()).isEqualTo("Canasta quincenal");
        assertThat(actualizado.getTotal()).isEqualByComparingTo("49990");
    }

    @Test
    void eliminarPedidoInexistenteLanza404YNoBorra() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.eliminar(1L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(pedidoRepository, never()).delete(any());
    }
}
