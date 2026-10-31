package com.grupo3.EcoMarket.controller;

import com.grupo3.EcoMarket.repository.ProductoRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductoControllerIT {

    private static final String PRODUCTO_VALIDO = """
            {"nombre": "Manzanas orgánicas", "descripcion": "Sin pesticidas", "stock": 100, "precio": 1500.50}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    private long crearProducto(String json) throws Exception {
        String respuesta = mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(respuesta, "$.id")).longValue();
    }

    @Test
    void crearYActualizarProducto() throws Exception {
        long id = crearProducto(PRODUCTO_VALIDO);

        mockMvc.perform(put("/api/productos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Peras", "stock": 5, "precio": 990}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Peras"));
    }

    @Test
    void actualizarProductoInexistenteDevuelve404YNoLoCrea() throws Exception {
        long antes = productoRepository.count();

        mockMvc.perform(put("/api/productos/{id}", 9999).contentType(MediaType.APPLICATION_JSON).content(PRODUCTO_VALIDO))
                .andExpect(status().isNotFound());

        assertThat(productoRepository.count()).isEqualTo(antes);
    }

    @Test
    void crearConIdNoSobrescribeProductoExistente() throws Exception {
        long id = crearProducto(PRODUCTO_VALIDO);

        long otroId = crearProducto("""
                {"id": %d, "nombre": "Intruso", "stock": 1, "precio": 1}
                """.formatted(id));

        assertThat(otroId).isNotEqualTo(id);
        assertThat(productoRepository.findById(id).orElseThrow().getNombre()).isEqualTo("Manzanas orgánicas");
    }

    @Test
    void productoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "", "stock": -1, "precio": 0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.stock").exists())
                .andExpect(jsonPath("$.errores.precio").exists());
    }

    @Test
    void productoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/productos/{id}", 9999)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/productos/{id}", 9999)).andExpect(status().isNotFound());
    }
}
