package com.grupo3.EcoMarket.controller;

import com.grupo3.EcoMarket.repository.UsuarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuarioControllerTest {

    private static final String USUARIO_VALIDO = """
            {"nombre": "Ana", "correo": "ana@email.com", "contrasena": "segura123"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private long crearUsuario(String json) throws Exception {
        String respuesta = mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(respuesta, "$.id")).longValue();
    }

    @Test
    void crearUsuarioNoDevuelveContrasenaYLaGuardaHasheada() throws Exception {
        long id = crearUsuario(USUARIO_VALIDO);

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("ana@email.com"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        String guardada = usuarioRepository.findById(id).orElseThrow().getContrasena();
        assertThat(guardada).isNotEqualTo("segura123");
        assertThat(passwordEncoder.matches("segura123", guardada)).isTrue();
    }

    @Test
    void crearUsuarioInvalidoDevuelve400ConErrores() throws Exception {
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "", "correo": "no-es-correo", "contrasena": "123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.correo").exists())
                .andExpect(jsonPath("$.errores.contrasena").exists());
    }

    @Test
    void crearUsuarioConCorreoDuplicadoDevuelve409() throws Exception {
        crearUsuario(USUARIO_VALIDO);

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(USUARIO_VALIDO))
                .andExpect(status().isConflict());
    }

    @Test
    void usuarioInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", 9999)).andExpect(status().isNotFound());
        mockMvc.perform(put("/api/usuarios/{id}", 9999).contentType(MediaType.APPLICATION_JSON).content(USUARIO_VALIDO))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/usuarios/{id}", 9999)).andExpect(status().isNotFound());
    }
}
