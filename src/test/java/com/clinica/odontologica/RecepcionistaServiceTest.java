package com.clinica.odontologica;

import com.clinica.odontologica.Dto.RecepcionistaRequestDTO;
import com.clinica.odontologica.Dto.RecepcionistaResponseDTO;
import com.clinica.odontologica.Entity.Recepcionista;
import com.clinica.odontologica.Exception.DuplicatedRecepcionistaException;
import com.clinica.odontologica.Exception.InvalidEmailException;
import com.clinica.odontologica.Exception.RecepcionistaNotFoundException;
import com.clinica.odontologica.Repository.RecepcionistaRepository;
import com.clinica.odontologica.Service.RecepcionistaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecepcionistaServiceTest {

    @Mock
    private RecepcionistaRepository recepcionistaRepository;

    @InjectMocks
    private RecepcionistaService recepcionistaService;

    private Recepcionista recepcionista;
    private RecepcionistaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        recepcionista = Recepcionista.builder()
                .id(1L)
                .nombre("Marcos")
                .apellido("Rodriguez")
                .email("marcosrodriguez@up.edu.ar")
                .telefono("9988774466")
                .usuario("marcosrodriguez")
                .build();

        requestDTO = RecepcionistaRequestDTO.builder()
                .nombre("Marcos")
                .apellido("Rodriguez")
                .email("marcosrodriguez@up.edu.ar")
                .telefono("9988774466")
                .usuario("marcosrodriguez")
                .build();
    }

    @Test
    @DisplayName("Crear recepcionista con datos válidos - debe retornar recepcionista creado")
    void crearRecepcionistaValido() {

        when(recepcionistaRepository.existsByEmail(anyString())).thenReturn(false);
        when(recepcionistaRepository.existsByUsuario(anyString())).thenReturn(false);
        when(recepcionistaRepository.save(any(Recepcionista.class))).thenReturn(recepcionista);

        RecepcionistaResponseDTO resultado =
                recepcionistaService.registrarRecepcionista(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Marcos");
        assertThat(resultado.getApellido()).isEqualTo("Rodriguez");
        assertThat(resultado.getEmail()).isEqualTo("marcosrodriguez@up.edu.ar");
        assertThat(resultado.getTelefono()).isEqualTo("9988774466");
        assertThat(resultado.getUsuario()).isEqualTo("marcosrodriguez");

        verify(recepcionistaRepository, times(1)).existsByEmail("marcosrodriguez@up.edu.ar");
        verify(recepcionistaRepository, times(1)).existsByUsuario("marcosrodriguez");
        verify(recepcionistaRepository, times(1)).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Crear recepcionista con email duplicado - debe lanzar DuplicatedRecepcionistaException")
    void crearRecepcionistaConEmailDuplicado() {

        when(recepcionistaRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> recepcionistaService.registrarRecepcionista(requestDTO))
                .isInstanceOf(DuplicatedRecepcionistaException.class)
                .hasMessageContaining("Ya existe un recepcionista con dicho email");

        verify(recepcionistaRepository, times(1)).existsByEmail("marcosrodriguez@up.edu.ar");
        verify(recepcionistaRepository, never()).existsByUsuario(anyString());
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Crear recepcionista con usuario duplicado - debe lanzar DuplicatedRecepcionistaException")
    void crearRecepcionistaConUsuarioDuplicado() {

        when(recepcionistaRepository.existsByEmail(anyString())).thenReturn(false);
        when(recepcionistaRepository.existsByUsuario(anyString())).thenReturn(true);

        assertThatThrownBy(() -> recepcionistaService.registrarRecepcionista(requestDTO))
                .isInstanceOf(DuplicatedRecepcionistaException.class)
                .hasMessageContaining("Ya existe un recepcionista con dicho usuario");

        verify(recepcionistaRepository, times(1)).existsByEmail("marcosrodriguez@up.edu.ar");
        verify(recepcionistaRepository, times(1)).existsByUsuario("marcosrodriguez");
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Crear recepcionista con email inválido - debe lanzar InvalidEmailException")
    void crearRecepcionistaConEmailInvalido() {

        requestDTO.setEmail("marcos@");

        assertThatThrownBy(() -> recepcionistaService.registrarRecepcionista(requestDTO))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("no tiene un formato válido");

        verify(recepcionistaRepository, never()).existsByEmail(anyString());
        verify(recepcionistaRepository, never()).existsByUsuario(anyString());
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Crear recepcionista sin nombre - debe lanzar IllegalArgumentException")
    void crearRecepcionistaSinNombre() {

        requestDTO.setNombre("");

        assertThatThrownBy(() -> recepcionistaService.registrarRecepcionista(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre es obligatorio");

        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Crear recepcionista sin usuario - debe lanzar IllegalArgumentException")
    void crearRecepcionistaSinUsuario() {

        requestDTO.setUsuario("");

        assertThatThrownBy(() -> recepcionistaService.registrarRecepcionista(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El usuario es obligatorio");

        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Buscar recepcionista por ID existente - debe retornar recepcionista")
    void buscarRecepcionistaPorIdExistente() {

        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));

        RecepcionistaResponseDTO resultado =
                recepcionistaService.buscarRecepcionista(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Marcos");
        assertThat(resultado.getUsuario()).isEqualTo("marcosrodriguez");

        verify(recepcionistaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar recepcionista por ID inexistente - debe lanzar RecepcionistaNotFoundException")
    void buscarRecepcionistaPorIdInexistente() {

        when(recepcionistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recepcionistaService.buscarRecepcionista(99L))
                .isInstanceOf(RecepcionistaNotFoundException.class)
                .hasMessageContaining("No se encontró el recepcionista con id");

        verify(recepcionistaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Listar recepcionistas - debe retornar lista de recepcionistas")
    void listarRecepcionistas() {

        Recepcionista recepcionista2 = Recepcionista.builder()
                .id(2L)
                .nombre("Laura")
                .apellido("Gomez")
                .email("lauragomez@up.edu.ar")
                .telefono("1122334455")
                .usuario("lauragomez")
                .build();

        when(recepcionistaRepository.findAll())
                .thenReturn(Arrays.asList(recepcionista, recepcionista2));

        List<RecepcionistaResponseDTO> resultado =
                recepcionistaService.listarTodos();

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Marcos");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Laura");

        verify(recepcionistaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Actualizar recepcionista con datos válidos - debe retornar recepcionista actualizado")
    void actualizarRecepcionistaValido() {

        RecepcionistaRequestDTO requestActualizado = RecepcionistaRequestDTO.builder()
                .nombre("Marcos")
                .apellido("Rodriguez")
                .email("marcosactualizado@up.edu.ar")
                .telefono("1111222233")
                .usuario("marcosactualizado")
                .build();

        Recepcionista recepcionistaActualizado = Recepcionista.builder()
                .id(1L)
                .nombre("Marcos")
                .apellido("Rodriguez")
                .email("marcosactualizado@up.edu.ar")
                .telefono("1111222233")
                .usuario("marcosactualizado")
                .build();

        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(recepcionistaRepository.existsByEmail("marcosactualizado@up.edu.ar")).thenReturn(false);
        when(recepcionistaRepository.existsByUsuario("marcosactualizado")).thenReturn(false);
        when(recepcionistaRepository.save(any(Recepcionista.class))).thenReturn(recepcionistaActualizado);

        RecepcionistaResponseDTO resultado =
                recepcionistaService.actualizarRecepcionista(1L, requestActualizado);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEmail()).isEqualTo("marcosactualizado@up.edu.ar");
        assertThat(resultado.getTelefono()).isEqualTo("1111222233");
        assertThat(resultado.getUsuario()).isEqualTo("marcosactualizado");

        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).existsByEmail("marcosactualizado@up.edu.ar");
        verify(recepcionistaRepository, times(1)).existsByUsuario("marcosactualizado");
        verify(recepcionistaRepository, times(1)).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Actualizar recepcionista inexistente - debe lanzar RecepcionistaNotFoundException")
    void actualizarRecepcionistaInexistente() {

        when(recepcionistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recepcionistaService.actualizarRecepcionista(99L, requestDTO))
                .isInstanceOf(RecepcionistaNotFoundException.class)
                .hasMessageContaining("No se encontró el recepcionista con id");

        verify(recepcionistaRepository, times(1)).findById(99L);
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Actualizar recepcionista con email duplicado - debe lanzar DuplicatedRecepcionistaException")
    void actualizarRecepcionistaConEmailDuplicado() {

        RecepcionistaRequestDTO requestActualizado = RecepcionistaRequestDTO.builder()
                .nombre("Marcos")
                .apellido("Rodriguez")
                .email("otroemail@up.edu.ar")
                .telefono("9988774466")
                .usuario("marcosrodriguez")
                .build();

        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(recepcionistaRepository.existsByEmail("otroemail@up.edu.ar")).thenReturn(true);

        assertThatThrownBy(() -> recepcionistaService.actualizarRecepcionista(1L, requestActualizado))
                .isInstanceOf(DuplicatedRecepcionistaException.class)
                .hasMessageContaining("Ya existe un recepcionista con dicho email");

        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).existsByEmail("otroemail@up.edu.ar");
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Actualizar recepcionista con usuario duplicado - debe lanzar DuplicatedRecepcionistaException")
    void actualizarRecepcionistaConUsuarioDuplicado() {

        RecepcionistaRequestDTO requestActualizado = RecepcionistaRequestDTO.builder()
                .nombre("Marcos")
                .apellido("Rodriguez")
                .email("marcosrodriguez@up.edu.ar")
                .telefono("9988774466")
                .usuario("usuarioexistente")
                .build();

        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(recepcionistaRepository.existsByUsuario("usuarioexistente")).thenReturn(true);

        assertThatThrownBy(() -> recepcionistaService.actualizarRecepcionista(1L, requestActualizado))
                .isInstanceOf(DuplicatedRecepcionistaException.class)
                .hasMessageContaining("Ya existe un recepcionista con dicho usuario");

        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).existsByUsuario("usuarioexistente");
        verify(recepcionistaRepository, never()).save(any(Recepcionista.class));
    }

    @Test
    @DisplayName("Eliminar recepcionista existente - debe eliminar correctamente")
    void eliminarRecepcionistaExistente() {

        when(recepcionistaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(recepcionistaRepository).deleteById(1L);

        recepcionistaService.eliminarRecepcionista(1L);

        verify(recepcionistaRepository, times(1)).existsById(1L);
        verify(recepcionistaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar recepcionista inexistente - debe lanzar RecepcionistaNotFoundException")
    void eliminarRecepcionistaInexistente() {

        when(recepcionistaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> recepcionistaService.eliminarRecepcionista(99L))
                .isInstanceOf(RecepcionistaNotFoundException.class)
                .hasMessageContaining("No se encontró el recepcionista con id");

        verify(recepcionistaRepository, times(1)).existsById(99L);
        verify(recepcionistaRepository, never()).deleteById(99L);
    }
}