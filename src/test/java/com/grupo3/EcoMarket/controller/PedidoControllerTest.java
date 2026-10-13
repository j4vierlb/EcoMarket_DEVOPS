package com.grupo3.EcoMarket.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearPedidoSinFechaUsaLaFechaActualYSePuedeActualizar() throws Exception {
        String respuesta = mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descripcion": "Canasta semanal", "total": 25990}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fecha").value(LocalDate.now().toString()))
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(respuesta, "$.id")).longValue();

        mockMvc.perform(put("/api/pedidos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descripcion": "Canasta quincenal", "total": 49990}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Canasta quincenal"))
                .andExpect(jsonPath("$.fecha").value(LocalDate.now().toString()));

        mockMvc.perform(delete("/api/pedidos/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/pedidos/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void pedidoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descripcion": "", "total": -5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.descripcion").exists())
                .andExpect(jsonPath("$.errores.total").exists());
    }
}
