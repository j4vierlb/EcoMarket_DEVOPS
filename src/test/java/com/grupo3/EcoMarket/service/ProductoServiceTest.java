package com.grupo3.EcoMarket.service;

import com.grupo3.EcoMarket.model.Producto;
import com.grupo3.EcoMarket.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto(Long id, String nombre) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setStock(10);
        p.setPrecio(new BigDecimal("1500"));
        return p;
    }

    @Test
    void listarDevuelveLosProductosDelRepositorio() {
        when(repository.findAll()).thenReturn(List.of(producto(1L, "Manzanas")));

        assertThat(productoService.listar()).hasSize(1);
    }

    @Test
    void crearIgnoraElIdRecibido() {
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(productoService.crear(producto(5L, "Manzanas")).getId()).isNull();
    }

    @Test
    void actualizarCopiaLosCamposSobreElProductoExistente() {
        Producto existente = producto(1L, "Manzanas");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Producto cambios = producto(null, "Peras");
        cambios.setDescripcion("De temporada");
        cambios.setStock(3);
        cambios.setPrecio(new BigDecimal("990"));
        Producto actualizado = productoService.actualizar(1L, cambios);

        assertThat(actualizado.getId()).isEqualTo(1L);
        assertThat(actualizado.getNombre()).isEqualTo("Peras");
        assertThat(actualizado.getDescripcion()).isEqualTo("De temporada");
        assertThat(actualizado.getStock()).isEqualTo(3);
        assertThat(actualizado.getPrecio()).isEqualByComparingTo("990");
    }

    @Test
    void obtenerProductoInexistenteLanza404() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerPorId(1L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void eliminarProductoExistenteLoBorra() {
        Producto existente = producto(1L, "Manzanas");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        productoService.eliminar(1L);

        verify(repository).delete(existente);
    }
}
