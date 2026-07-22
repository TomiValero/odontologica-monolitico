package com.clinica.odontologica;

import com.clinica.odontologica.Dto.TurnoRequestDTO;
import com.clinica.odontologica.Dto.TurnoResponseDTO;
import com.clinica.odontologica.Entity.Odontologo;
import com.clinica.odontologica.Entity.Paciente;
import com.clinica.odontologica.Entity.Recepcionista;
import com.clinica.odontologica.Entity.Turno;
import com.clinica.odontologica.Exception.BusinessRuleException;
import com.clinica.odontologica.Exception.RecepcionistaNotFoundException;
import com.clinica.odontologica.Exception.ResourceNotFoundException;
import com.clinica.odontologica.Exception.TurnoConflictException;
import com.clinica.odontologica.Repository.OdontologoRepository;
import com.clinica.odontologica.Repository.PacienteRepository;
import com.clinica.odontologica.Repository.RecepcionistaRepository;
import com.clinica.odontologica.Repository.TurnoRepository;
import com.clinica.odontologica.Service.TurnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private OdontologoRepository odontologoRepository;

    @Mock
    private RecepcionistaRepository recepcionistaRepository;

    @InjectMocks
    private TurnoService turnoService;

    private Paciente paciente;
    private Odontologo odontologo;
    private Recepcionista recepcionista;
    private Turno turno;
    private TurnoRequestDTO requestDTO;
    private LocalDateTime fechaTurno;

    @BeforeEach
    void setUp() {

        fechaTurno = LocalDateTime.now().plusDays(1);

        paciente = Paciente.builder()
                .id(1L)
                .nombre("Homero")
                .apellido("Simpson")
                .cedula("111112")
                .email("homersimpson@up.edu.ar")
                .build();

        odontologo = Odontologo.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .matricula("MAT123")
                .telefono("1122334455")
                .email("juanperez@up.edu.ar")
                .especialidad("Ortodoncia")
                .build();

        recepcionista = Recepcionista.builder()
                .id(1L)
                .nombre("Marcos")
                .apellido("Rodriguez")
                .telefono("9988774466")
                .email("marcosrodriguez@up.edu.ar")
                .usuario("marcosrodriguez")
                .build();

        turno = Turno.builder()
                .id(1L)
                .paciente(paciente)
                .odontologo(odontologo)
                .recepcionista(recepcionista)
                .fechaTurno(fechaTurno)
                .estado("PROGRAMADO")
                .observaciones("Primera consulta")
                .build();

        requestDTO = TurnoRequestDTO.builder()
                .pacienteId(1L)
                .odontologoId(1L)
                .recepcionistaId(1L)
                .fechaTurno(fechaTurno)
                .estado("PROGRAMADO")
                .observaciones("Primera consulta")
                .build();
    }

    @Test
    @DisplayName("Crear turno con datos válidos - debe retornar turno creado")
    void crearTurnoValido() {

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(turnoRepository.findByOdontologoIdAndFechaTurno(1L, fechaTurno))
                .thenReturn(Optional.empty());
        when(turnoRepository.save(any(Turno.class))).thenReturn(turno);

        TurnoResponseDTO resultado = turnoService.registrarTurno(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPacienteId()).isEqualTo(1L);
        assertThat(resultado.getPacienteNombre()).isEqualTo("Homero");
        assertThat(resultado.getOdontologoId()).isEqualTo(1L);
        assertThat(resultado.getOdontologoNombre()).isEqualTo("Juan");
        assertThat(resultado.getOdontologoMatricula()).isEqualTo("MAT123");
        assertThat(resultado.getRecepcionistaId()).isEqualTo(1L);
        assertThat(resultado.getRecepcionistaNombre()).isEqualTo("Marcos");
        assertThat(resultado.getEstado()).isEqualTo("PROGRAMADO");

        verify(pacienteRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(turnoRepository, times(1)).findByOdontologoIdAndFechaTurno(1L, fechaTurno);
        verify(turnoRepository, times(1)).save(any(Turno.class));
    }

    @Test
    @DisplayName("Crear turno con paciente inexistente - debe lanzar ResourceNotFoundException")
    void crearTurnoConPacienteInexistente() {

        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.registrarTurno(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el paciente con id");

        verify(pacienteRepository, times(1)).findById(1L);
        verify(odontologoRepository, never()).findById(any());
        verify(recepcionistaRepository, never()).findById(any());
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Crear turno con odontólogo inexistente - debe lanzar ResourceNotFoundException")
    void crearTurnoConOdontologoInexistente() {

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(odontologoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.registrarTurno(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el odontólogo con id");

        verify(pacienteRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, never()).findById(any());
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Crear turno con recepcionista inexistente - debe lanzar RecepcionistaNotFoundException")
    void crearTurnoConRecepcionistaInexistente() {

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.registrarTurno(requestDTO))
                .isInstanceOf(RecepcionistaNotFoundException.class)
                .hasMessageContaining("No se encontró el recepcionista con id");

        verify(pacienteRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Crear turno con horario ocupado - debe lanzar TurnoConflictException")
    void crearTurnoConHorarioOcupado() {

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(turnoRepository.findByOdontologoIdAndFechaTurno(1L, fechaTurno))
                .thenReturn(Optional.of(turno));

        assertThatThrownBy(() -> turnoService.registrarTurno(requestDTO))
                .isInstanceOf(TurnoConflictException.class)
                .hasMessageContaining("Ya existe un turno para ese odontólogo");

        verify(pacienteRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(turnoRepository, times(1)).findByOdontologoIdAndFechaTurno(1L, fechaTurno);
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Crear turno con fecha en pasado - debe lanzar TurnoConflictException")
    void crearTurnoConFechaEnPasado() {

        requestDTO.setFechaTurno(LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> turnoService.registrarTurno(requestDTO))
                .isInstanceOf(TurnoConflictException.class)
                .hasMessageContaining("No se puede registrar un turno en el pasado");

        verify(pacienteRepository, never()).findById(any());
        verify(odontologoRepository, never()).findById(any());
        verify(recepcionistaRepository, never()).findById(any());
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Crear turno con estado inválido - debe lanzar BusinessRuleException")
    void crearTurnoConEstadoInvalido() {

        requestDTO.setEstado("PENDIENTE");

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(turnoRepository.findByOdontologoIdAndFechaTurno(1L, fechaTurno))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.registrarTurno(requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El estado del turno debe ser PROGRAMADO, CANCELADO o COMPLETADO");

        verify(pacienteRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(turnoRepository, times(1)).findByOdontologoIdAndFechaTurno(1L, fechaTurno);
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Buscar turno por ID existente - debe retornar turno")
    void buscarTurnoPorIdExistente() {

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        TurnoResponseDTO resultado = turnoService.buscarTurno(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPacienteNombre()).isEqualTo("Homero");
        assertThat(resultado.getOdontologoNombre()).isEqualTo("Juan");
        assertThat(resultado.getRecepcionistaNombre()).isEqualTo("Marcos");

        verify(turnoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar turno por ID inexistente - debe lanzar ResourceNotFoundException")
    void buscarTurnoPorIdInexistente() {

        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.buscarTurno(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el turno con id");

        verify(turnoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Listar turnos - debe retornar lista de turnos")
    void listarTurnos() {

        Turno turno2 = Turno.builder()
                .id(2L)
                .paciente(paciente)
                .odontologo(odontologo)
                .recepcionista(recepcionista)
                .fechaTurno(LocalDateTime.now().plusDays(2))
                .estado("PROGRAMADO")
                .observaciones("Control")
                .build();

        when(turnoRepository.findAll()).thenReturn(Arrays.asList(turno, turno2));

        List<TurnoResponseDTO> resultado = turnoService.listarTodos();

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(1).getId()).isEqualTo(2L);
        assertThat(resultado.get(0).getRecepcionistaId()).isEqualTo(1L);
        assertThat(resultado.get(0).getRecepcionistaNombre()).isEqualTo("Marcos");

        verify(turnoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Actualizar turno con datos válidos - debe retornar turno actualizado")
    void actualizarTurnoValido() {

        TurnoRequestDTO requestActualizado = TurnoRequestDTO.builder()
                .pacienteId(1L)
                .odontologoId(1L)
                .recepcionistaId(1L)
                .fechaTurno(fechaTurno.plusHours(1))
                .estado("COMPLETADO")
                .observaciones("Consulta finalizada")
                .build();

        Turno turnoActualizado = Turno.builder()
                .id(1L)
                .paciente(paciente)
                .odontologo(odontologo)
                .recepcionista(recepcionista)
                .fechaTurno(fechaTurno.plusHours(1))
                .estado("COMPLETADO")
                .observaciones("Consulta finalizada")
                .build();

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(turnoRepository.findByOdontologoIdAndFechaTurno(1L, fechaTurno.plusHours(1)))
                .thenReturn(Optional.empty());
        when(turnoRepository.save(any(Turno.class))).thenReturn(turnoActualizado);

        TurnoResponseDTO resultado = turnoService.actualizarTurno(1L, requestActualizado);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo("COMPLETADO");
        assertThat(resultado.getObservaciones()).isEqualTo("Consulta finalizada");
        assertThat(resultado.getRecepcionistaId()).isEqualTo(1L);
        assertThat(resultado.getRecepcionistaNombre()).isEqualTo("Marcos");

        verify(turnoRepository, times(1)).findById(1L);
        verify(pacienteRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(turnoRepository, times(1)).save(any(Turno.class));
    }

    @Test
    @DisplayName("Actualizar turno inexistente - debe lanzar ResourceNotFoundException")
    void actualizarTurnoInexistente() {

        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.actualizarTurno(99L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el turno con id");

        verify(turnoRepository, times(1)).findById(99L);
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Actualizar turno con horario ocupado por otro turno - debe lanzar TurnoConflictException")
    void actualizarTurnoConHorarioOcupadoPorOtroTurno() {

        Turno otroTurno = Turno.builder()
                .id(2L)
                .paciente(paciente)
                .odontologo(odontologo)
                .recepcionista(recepcionista)
                .fechaTurno(fechaTurno)
                .estado("PROGRAMADO")
                .observaciones("Otro turno")
                .build();

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(turnoRepository.findByOdontologoIdAndFechaTurno(1L, fechaTurno))
                .thenReturn(Optional.of(otroTurno));

        assertThatThrownBy(() -> turnoService.actualizarTurno(1L, requestDTO))
                .isInstanceOf(TurnoConflictException.class)
                .hasMessageContaining("Ya existe un turno para ese odontólogo");

        verify(turnoRepository, times(1)).findById(1L);
        verify(pacienteRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Eliminar turno existente por recepcionista existente - debe eliminar correctamente")
    void eliminarTurnoExistente() {

        when(turnoRepository.existsById(1L)).thenReturn(true);
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        doNothing().when(turnoRepository).deleteById(1L);

        turnoService.eliminarTurno(1L, 1L);

        verify(turnoRepository, times(1)).existsById(1L);
        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(turnoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar turno inexistente - debe lanzar ResourceNotFoundException")
    void eliminarTurnoInexistente() {

        when(turnoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> turnoService.eliminarTurno(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el turno con id");

        verify(turnoRepository, times(1)).existsById(99L);
        verify(recepcionistaRepository, never()).findById(any());
        verify(turnoRepository, never()).deleteById(99L);
    }

    @Test
    @DisplayName("Buscar turnos por odontólogo existente - debe retornar lista")
    void buscarTurnosPorOdontologoExistente() {

        when(odontologoRepository.existsById(1L)).thenReturn(true);
        when(turnoRepository.findByOdontologoId(1L)).thenReturn(Arrays.asList(turno));

        List<TurnoResponseDTO> resultado = turnoService.buscarTurnosPorOdontologo(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(1);
        assertThat(resultado.get(0).getOdontologoId()).isEqualTo(1L);

        verify(odontologoRepository, times(1)).existsById(1L);
        verify(turnoRepository, times(1)).findByOdontologoId(1L);
    }

    @Test
    @DisplayName("Buscar turnos por odontólogo inexistente - debe lanzar ResourceNotFoundException")
    void buscarTurnosPorOdontologoInexistente() {

        when(odontologoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> turnoService.buscarTurnosPorOdontologo(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el odontólogo con id");

        verify(odontologoRepository, times(1)).existsById(99L);
        verify(turnoRepository, never()).findByOdontologoId(any());
    }

    @Test
    @DisplayName("Buscar turnos por paciente existente - debe retornar lista")
    void buscarTurnosPorPacienteExistente() {

        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(turnoRepository.findByPacienteId(1L)).thenReturn(Arrays.asList(turno));

        List<TurnoResponseDTO> resultado = turnoService.buscarTurnosPorPaciente(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(1);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(1L);

        verify(pacienteRepository, times(1)).existsById(1L);
        verify(turnoRepository, times(1)).findByPacienteId(1L);
    }

    @Test
    @DisplayName("Buscar turnos por paciente inexistente - debe lanzar ResourceNotFoundException")
    void buscarTurnosPorPacienteInexistente() {

        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> turnoService.buscarTurnosPorPaciente(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el paciente con id");

        verify(pacienteRepository, times(1)).existsById(99L);
        verify(turnoRepository, never()).findByPacienteId(any());
    }

    @Test
    @DisplayName("Cancelar turno existente por recepcionista existente - debe retornar turno cancelado")
    void cancelarTurnoValido() {

        Turno turnoCancelado = Turno.builder()
                .id(1L)
                .paciente(paciente)
                .odontologo(odontologo)
                .recepcionista(recepcionista)
                .fechaTurno(fechaTurno)
                .estado("CANCELADO")
                .observaciones("Primera consulta")
                .build();

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(recepcionistaRepository.findById(1L)).thenReturn(Optional.of(recepcionista));
        when(turnoRepository.save(any(Turno.class))).thenReturn(turnoCancelado);

        TurnoResponseDTO resultado = turnoService.cancelarTurno(1L, 1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo("CANCELADO");
        assertThat(resultado.getRecepcionistaId()).isEqualTo(1L);
        assertThat(resultado.getRecepcionistaNombre()).isEqualTo("Marcos");

        verify(turnoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).findById(1L);
        verify(turnoRepository, times(1)).save(any(Turno.class));
    }

    @Test
    @DisplayName("Cancelar turno inexistente - debe lanzar ResourceNotFoundException")
    void cancelarTurnoInexistente() {

        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.cancelarTurno(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el turno con id");

        verify(turnoRepository, times(1)).findById(99L);
        verify(recepcionistaRepository, never()).findById(any());
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    @DisplayName("Cancelar turno con recepcionista inexistente - debe lanzar RecepcionistaNotFoundException")
    void cancelarTurnoConRecepcionistaInexistente() {

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(recepcionistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.cancelarTurno(1L, 99L))
                .isInstanceOf(RecepcionistaNotFoundException.class)
                .hasMessageContaining("No se encontró el recepcionista con id");

        verify(turnoRepository, times(1)).findById(1L);
        verify(recepcionistaRepository, times(1)).findById(99L);
        verify(turnoRepository, never()).save(any(Turno.class));
    }
}