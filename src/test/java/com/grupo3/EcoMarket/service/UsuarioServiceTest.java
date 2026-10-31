package com.grupo3.EcoMarket.service;

import com.grupo3.EcoMarket.model.Usuario;
import com.grupo3.EcoMarket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario(Long id, String correo) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Ana");
        u.setCorreo(correo);
        u.setContrasena("segura123");
        return u;
    }

    @Test
    void obtenerTodosDevuelveLosUsuariosDelRepositorio() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario(1L, "ana@email.com")));

        assertThat(usuarioService.obtenerTodos()).hasSize(1);
    }

    @Test
    void crearUsuarioHasheaLaContrasenaEIgnoraElId() {
        when(usuarioRepository.existsByCorreo("ana@email.com")).thenReturn(false);
        when(passwordEncoder.encode("segura123")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = usuarioService.crearUsuario(usuario(99L, "ana@email.com"));

        assertThat(creado.getId()).isNull();
        assertThat(creado.getContrasena()).isEqualTo("hash");
    }

    @Test
    void crearUsuarioConCorreoExistenteLanza409YNoGuarda() {
        when(usuarioRepository.existsByCorreo("ana@email.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(usuario(null, "ana@email.com")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actualizarUsuarioModificaLosCamposYHasheaLaNuevaContrasena() {
        Usuario existente = usuario(1L, "ana@email.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCorreoAndIdNot("nueva@email.com", 1L)).thenReturn(false);
        when(passwordEncoder.encode("otraClave1")).thenReturn("hash2");
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario cambios = usuario(null, "nueva@email.com");
        cambios.setNombre("Ana María");
        cambios.setContrasena("otraClave1");
        Usuario actualizado = usuarioService.actualizarUsuario(1L, cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Ana María");
        assertThat(actualizado.getCorreo()).isEqualTo("nueva@email.com");
        assertThat(actualizado.getContrasena()).isEqualTo("hash2");
    }

    @Test
    void actualizarUsuarioConCorreoDeOtroUsuarioLanza409() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "ana@email.com")));
        when(usuarioRepository.existsByCorreoAndIdNot("otro@email.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.actualizarUsuario(1L, usuario(null, "otro@email.com")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void actualizarUsuarioInexistenteLanza404() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.actualizarUsuario(1L, usuario(null, "ana@email.com")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void eliminarUsuarioExistenteLoBorra() {
        Usuario existente = usuario(1L, "ana@email.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository).delete(existente);
    }
}
