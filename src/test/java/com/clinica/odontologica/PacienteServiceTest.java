package com.clinica.odontologica;

import com.clinica.odontologica.Dto.DomicilioRequestDTO;
import com.clinica.odontologica.Dto.DomicilioResponseDTO;
import com.clinica.odontologica.Dto.PacienteRequestDTO;
import com.clinica.odontologica.Dto.PacienteResponseDTO;
import com.clinica.odontologica.Entity.Domicilio;
import com.clinica.odontologica.Entity.Paciente;
import com.clinica.odontologica.Exception.DuplicatedResourceException;
import com.clinica.odontologica.Exception.InvalidEmailException;
import com.clinica.odontologica.Exception.ResourceNotFoundException;
import com.clinica.odontologica.Repository.PacienteRepository;
import com.clinica.odontologica.Service.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente;
    private PacienteRequestDTO requestDTO;
    private PacienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {

        Domicilio domicilio = Domicilio.builder()
                .id(1L)
                .calle("Av siempre viva")
                .localidad("Springfield")
                .provincia("Buenos Aires")
                .build();

        DomicilioRequestDTO domicilioRequestDTO = DomicilioRequestDTO.builder()
                .calle("Av siempre viva")
                .localidad("Springfield")
                .provincia("Buenos Aires")
                .build();

        DomicilioResponseDTO domicilioResponseDTO = DomicilioResponseDTO.builder()
                .id(1L)
                .calle("Av siempre viva")
                .localidad("Springfield")
                .provincia("Buenos Aires")
                .build();

        paciente = Paciente.builder()
                .id(1L)
                .nombre("Homero")
                .apellido("Simpson")
                .cedula("111112")
                .fechaIngreso(LocalDate.now())
                .email("homersimpson@up.edu.ar")
                .domicilio(domicilio)
                .build();

        requestDTO = PacienteRequestDTO.builder()
                .nombre("Homero")
                .apellido("Simpson")
                .cedula("111112")
                .fechaIngreso(LocalDate.now())
                .email("homersimpson@up.edu.ar")
                .domicilio(domicilioRequestDTO)
                .build();

        responseDTO = PacienteResponseDTO.builder()
                .id(1L)
                .nombre("Homero")
                .apellido("Simpson")
                .cedula("111112")
                .fechaIngreso(LocalDate.now())
                .email("homersimpson@up.edu.ar")
                .domicilio(domicilioResponseDTO)
                .build();
    }

    @Test
    @DisplayName("Crear paciente con datos válidos - debe retornar paciente creado")
    void crearPacienteValido() {

        when(pacienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(pacienteRepository.existsByCedula(anyString())).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        PacienteResponseDTO resultado = pacienteService.registrarPaciente(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Homero");
        assertThat(resultado.getApellido()).isEqualTo("Simpson");
        assertThat(resultado.getCedula()).isEqualTo("111112");
        assertThat(resultado.getEmail()).isEqualTo("homersimpson@up.edu.ar");
        assertThat(resultado.getDomicilio()).isNotNull();
        assertThat(resultado.getDomicilio().getCalle()).isEqualTo("Av siempre viva");

        verify(pacienteRepository, times(1)).existsByEmail("homersimpson@up.edu.ar");
        verify(pacienteRepository, times(1)).existsByCedula("111112");
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Crear paciente con email duplicado - debe lanzar DuplicatedResourceException")
    void crearPacienteConEmailDuplicado() {

        when(pacienteRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> pacienteService.registrarPaciente(requestDTO))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessageContaining("Ya existe un paciente con dicho email");

        verify(pacienteRepository, times(1)).existsByEmail("homersimpson@up.edu.ar");
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Crear paciente con cédula duplicada - debe lanzar DuplicatedResourceException")
    void crearPacienteConCedulaDuplicada() {

        when(pacienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(pacienteRepository.existsByCedula(anyString())).thenReturn(true);

        assertThatThrownBy(() -> pacienteService.registrarPaciente(requestDTO))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessageContaining("Ya existe un paciente con dicha cédula");

        verify(pacienteRepository, times(1)).existsByEmail("homersimpson@up.edu.ar");
        verify(pacienteRepository, times(1)).existsByCedula("111112");
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Crear paciente con email inválido - debe lanzar InvalidEmailException")
    void crearPacienteConEmailInvalido() {

        requestDTO.setEmail("homero@");

        assertThatThrownBy(() -> pacienteService.registrarPaciente(requestDTO))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("no tiene un formato válido");

        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Buscar paciente por ID existente - debe retornar paciente")
    void buscarPacientePorIdExistente() {

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        PacienteResponseDTO resultado = pacienteService.buscarPaciente(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Homero");

        verify(pacienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar paciente por ID inexistente - debe lanzar ResourceNotFoundException")
    void buscarPacientePorIdInexistente() {

        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.buscarPaciente(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el paciente con id");

        verify(pacienteRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Listar pacientes - debe retornar lista de pacientes")
    void listarPacientes() {

        Paciente paciente2 = Paciente.builder()
                .id(2L)
                .nombre("Marge")
                .apellido("Simpson")
                .cedula("222223")
                .fechaIngreso(LocalDate.now())
                .email("margesimpson@up.edu.ar")
                .domicilio(paciente.getDomicilio())
                .build();

        when(pacienteRepository.findAll()).thenReturn(Arrays.asList(paciente, paciente2));

        List<PacienteResponseDTO> resultado = pacienteService.listarTodos();

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Homero");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Marge");

        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Actualizar paciente con datos válidos - debe retornar paciente actualizado")
    void actualizarPacienteValido() {

        PacienteRequestDTO requestActualizado = PacienteRequestDTO.builder()
                .nombre("Homero")
                .apellido("Simpson")
                .cedula("111112")
                .fechaIngreso(LocalDate.now())
                .email("homeroactualizado@up.edu.ar")
                .domicilio(DomicilioRequestDTO.builder()
                        .calle("Calle nueva")
                        .localidad("Springfield")
                        .provincia("Buenos Aires")
                        .build())
                .build();

        Paciente pacienteActualizado = Paciente.builder()
                .id(1L)
                .nombre("Homero")
                .apellido("Simpson")
                .cedula("111112")
                .fechaIngreso(LocalDate.now())
                .email("homeroactualizado@up.edu.ar")
                .domicilio(Domicilio.builder()
                        .id(1L)
                        .calle("Calle nueva")
                        .localidad("Springfield")
                        .provincia("Buenos Aires")
                        .build())
                .build();

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.existsByEmail("homeroactualizado@up.edu.ar")).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteActualizado);

        PacienteResponseDTO resultado = pacienteService.actualizarPaciente(1L, requestActualizado);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEmail()).isEqualTo("homeroactualizado@up.edu.ar");
        assertThat(resultado.getDomicilio().getCalle()).isEqualTo("Calle nueva");

        verify(pacienteRepository, times(1)).findById(1L);
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Eliminar paciente existente - debe eliminar correctamente")
    void eliminarPacienteExistente() {

        when(pacienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pacienteRepository).deleteById(1L);

        pacienteService.eliminarPaciente(1L);

        verify(pacienteRepository, times(1)).existsById(1L);
        verify(pacienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar paciente inexistente - debe lanzar ResourceNotFoundException")
    void eliminarPacienteInexistente() {

        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> pacienteService.eliminarPaciente(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el paciente con id");

        verify(pacienteRepository, times(1)).existsById(99L);
        verify(pacienteRepository, never()).deleteById(99L);
    }
}