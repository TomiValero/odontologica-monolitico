package com.clinica.odontologica.Service;

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
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Builder
@Slf4j
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private RecepcionistaRepository recepcionistaRepository;

    @Transactional
    public TurnoResponseDTO registrarTurno(TurnoRequestDTO turnoRequestDTO) {

        log.info("Registrando nuevo turno para paciente ID: {} , odontólogo ID: {} y recepcionista ID: {}",
                turnoRequestDTO.getPacienteId(), turnoRequestDTO.getOdontologoId(), turnoRequestDTO.getRecepcionistaId());

        validarDatosObligatorios(turnoRequestDTO);
        validarFechaTurno(turnoRequestDTO.getFechaTurno());

        Paciente paciente = buscarPacienteExistente(turnoRequestDTO.getPacienteId());
        Odontologo odontologo = buscarOdontologoExistente(turnoRequestDTO.getOdontologoId());
        Recepcionista recepcionista = buscarRecepcionistaExistente(turnoRequestDTO.getRecepcionistaId());

        validarConflictoTurno(
                turnoRequestDTO.getOdontologoId(),
                turnoRequestDTO.getFechaTurno(),
                null
        );

        Turno turno = convertirRequestDtoAEntidad(turnoRequestDTO, paciente, odontologo, recepcionista);

        Turno turnoCreado = turnoRepository.save(turno);

        log.info("Turno creado correctamente con ID: {}", turnoCreado.getId());

        return convertirEntidadADtoResponse(turnoCreado);
    }

    @Transactional(readOnly = true)
    public TurnoResponseDTO buscarTurno(Long id) {

        log.info("Buscando turno por ID: {}", id);

        Turno turnoBuscado = turnoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Turno no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("No se encontró el turno con id: " + id);
                });

        return convertirEntidadADtoResponse(turnoBuscado);
    }

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> listarTodos() {

        log.info("Listando todos los turnos");

        List<Turno> turnoList = turnoRepository.findAll();

        List<TurnoResponseDTO> turnoDTOList = new ArrayList<>();

        for (Turno turno : turnoList) {
            turnoDTOList.add(convertirEntidadADtoResponse(turno));
        }

        return turnoDTOList;
    }

    @Transactional
    public TurnoResponseDTO actualizarTurno(Long id, TurnoRequestDTO turnoRequestDTO) {

        log.info("Actualizando turno con ID: {}", id);

        validarDatosObligatorios(turnoRequestDTO);
        validarFechaTurno(turnoRequestDTO.getFechaTurno());

        Turno turnoBuscado = turnoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Turno no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("No se encontró el turno con id: " + id);
                });

        Paciente paciente = buscarPacienteExistente(turnoRequestDTO.getPacienteId());
        Odontologo odontologo = buscarOdontologoExistente(turnoRequestDTO.getOdontologoId());
        Recepcionista recepcionista = buscarRecepcionistaExistente(turnoRequestDTO.getRecepcionistaId());

        validarConflictoTurno(
                turnoRequestDTO.getOdontologoId(),
                turnoRequestDTO.getFechaTurno(),
                id
        );

        turnoBuscado.setPaciente(paciente);
        turnoBuscado.setOdontologo(odontologo);
        turnoBuscado.setRecepcionista(recepcionista);
        turnoBuscado.setFechaTurno(turnoRequestDTO.getFechaTurno());
        turnoBuscado.setEstado(validarEstado(turnoRequestDTO.getEstado()));
        turnoBuscado.setObservaciones(turnoRequestDTO.getObservaciones());

        Turno turnoActualizado = turnoRepository.save(turnoBuscado);

        log.info("Turno actualizado correctamente con ID: {}", id);

        return convertirEntidadADtoResponse(turnoActualizado);
    }

    @Transactional
    public TurnoResponseDTO cancelarTurno(Long id, Long recepcionistaId) {

        log.info("Cancelando turno con ID: {} por recepcionista ID: {}", id, recepcionistaId);

        Turno turnoBuscado = turnoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Turno no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("No se encontró el turno con id: " + id);
                });

        Recepcionista recepcionista = buscarRecepcionistaExistente(recepcionistaId);

        turnoBuscado.setEstado("CANCELADO");
        turnoBuscado.setRecepcionista(recepcionista);

        Turno turnoCancelado = turnoRepository.save(turnoBuscado);

        log.info("Turno cancelado correctamente con ID: {}", id);

        return convertirEntidadADtoResponse(turnoCancelado);
    }

    @Transactional
    public void eliminarTurno(Long id, Long recepcionistaId) {

        log.info("Eliminando turno con ID: {} por recepcionista ID: {}", id, recepcionistaId);

        if (recepcionistaId == null) {
            throw new IllegalArgumentException("El recepcionista es obligatorio para eliminar el turno");
        }

        if (!turnoRepository.existsById(id)) {
            log.warn("Intento de eliminar turno inexistente con ID: {}", id);
            throw new ResourceNotFoundException("No se encontró el turno con id: " + id);
        }

        buscarRecepcionistaExistente(recepcionistaId);

        turnoRepository.deleteById(id);

        log.info("Turno eliminado correctamente con ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> buscarTurnosPorOdontologo(Long odontologoId) {

        log.info("Buscando turnos por odontólogo ID: {}", odontologoId);

        if (!odontologoRepository.existsById(odontologoId)) {
            log.warn("Odontólogo no encontrado con ID: {}", odontologoId);
            throw new ResourceNotFoundException("No se encontró el odontólogo con id: " + odontologoId);
        }

        List<Turno> turnoList = turnoRepository.findByOdontologoId(odontologoId);

        List<TurnoResponseDTO> turnoDTOList = new ArrayList<>();

        for (Turno turno : turnoList) {
            turnoDTOList.add(convertirEntidadADtoResponse(turno));
        }

        return turnoDTOList;
    }

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> buscarTurnosPorPaciente(Long pacienteId) {

        log.info("Buscando turnos por paciente ID: {}", pacienteId);

        if (!pacienteRepository.existsById(pacienteId)) {
            log.warn("Paciente no encontrado con ID: {}", pacienteId);
            throw new ResourceNotFoundException("No se encontró el paciente con id: " + pacienteId);
        }

        List<Turno> turnoList = turnoRepository.findByPacienteId(pacienteId);

        List<TurnoResponseDTO> turnoDTOList = new ArrayList<>();

        for (Turno turno : turnoList) {
            turnoDTOList.add(convertirEntidadADtoResponse(turno));
        }

        return turnoDTOList;
    }

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> buscarTurnosPorRecepcionista(Long recepcionistaId) {

        log.info("Buscando turnos por recepcionista ID: {}", recepcionistaId);

        if (!recepcionistaRepository.existsById(recepcionistaId)) {
            log.warn("Recepcionista no encontrado con ID: {}", recepcionistaId);
            throw new RecepcionistaNotFoundException("No se encontró el recepcionista con id: " + recepcionistaId);
        }

        List<Turno> turnoList = turnoRepository.findByRecepcionistaId(recepcionistaId);

        List<TurnoResponseDTO> turnoDTOList = new ArrayList<>();

        for (Turno turno : turnoList) {
            turnoDTOList.add(convertirEntidadADtoResponse(turno));
        }

        return turnoDTOList;
    }

    private void validarDatosObligatorios(TurnoRequestDTO turnoRequestDTO) {

        if (turnoRequestDTO.getPacienteId() == null) {
            throw new IllegalArgumentException("El paciente es obligatorio");
        }

        if (turnoRequestDTO.getOdontologoId() == null) {
            throw new IllegalArgumentException("El odontólogo es obligatorio");
        }

        if (turnoRequestDTO.getFechaTurno() == null) {
            throw new IllegalArgumentException("La fecha del turno es obligatoria");
        }

        if (turnoRequestDTO.getRecepcionistaId() == null) {
            throw new IllegalArgumentException("El recepcionista es obligatorio");
        }
    }

    private void validarFechaTurno(LocalDateTime fechaTurno) {

        if (fechaTurno.isBefore(LocalDateTime.now())) {
            log.warn("Intento de registrar turno en el pasado: {}", fechaTurno);
            throw new TurnoConflictException("No se puede registrar un turno en el pasado");
        }
    }

    private void validarConflictoTurno(Long odontologoId, LocalDateTime fechaTurno, Long turnoIdActual) {

        Optional<Turno> turnoExistente = turnoRepository.findByOdontologoIdAndFechaTurno(
                odontologoId,
                fechaTurno
        );

        if (turnoExistente.isPresent()) {

            if (turnoIdActual == null || !turnoExistente.get().getId().equals(turnoIdActual)) {

                log.warn("Conflicto de turno para odontólogo ID: {} en fecha: {}",
                        odontologoId, fechaTurno);

                throw new TurnoConflictException(
                        "Ya existe un turno para ese odontólogo en la fecha y horario indicado"
                );
            }
        }
    }

    private Paciente buscarPacienteExistente(Long pacienteId) {

        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> {
                    log.warn("Paciente no encontrado con ID: {}", pacienteId);
                    return new ResourceNotFoundException("No se encontró el paciente con id: " + pacienteId);
                });
    }

    private Odontologo buscarOdontologoExistente(Long odontologoId) {

        return odontologoRepository.findById(odontologoId)
                .orElseThrow(() -> {
                    log.warn("Odontólogo no encontrado con ID: {}", odontologoId);
                    return new ResourceNotFoundException("No se encontró el odontólogo con id: " + odontologoId);
                });
    }

    private Recepcionista buscarRecepcionistaExistente(Long recepcionistaId) {

        return recepcionistaRepository.findById(recepcionistaId)
                .orElseThrow(() -> {
                    log.warn("Recepcionista no encontrado con ID: {}", recepcionistaId);
                    return new RecepcionistaNotFoundException("No se encontró el recepcionista con id: " + recepcionistaId);
                });
    }

    private String validarEstado(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            log.warn("Estado de turno inválido: {}", estado);

            throw new BusinessRuleException(
                    "El estado del turno debe ser PROGRAMADO, CANCELADO o COMPLETADO"
            );
        }

        String estadoNormalizado = estado.trim().toUpperCase();

        if (!estadoNormalizado.equals("PROGRAMADO")
                && !estadoNormalizado.equals("CANCELADO")
                && !estadoNormalizado.equals("COMPLETADO")) {

            log.warn("Estado de turno inválido: {}", estado);

            throw new BusinessRuleException(
                    "El estado del turno debe ser PROGRAMADO, CANCELADO o COMPLETADO"
            );
        }

        return estadoNormalizado;
    }

    private Turno convertirRequestDtoAEntidad(
            TurnoRequestDTO turnoRequestDTO,
            Paciente paciente,
            Odontologo odontologo,
            Recepcionista recepcionista
    ) {

        return Turno.builder()
                .paciente(paciente)
                .odontologo(odontologo)
                .recepcionista(recepcionista)
                .fechaTurno(turnoRequestDTO.getFechaTurno())
                .estado(validarEstado(turnoRequestDTO.getEstado()))
                .observaciones(turnoRequestDTO.getObservaciones())
                .build();
    }

    private TurnoResponseDTO convertirEntidadADtoResponse(Turno turno) {

        return TurnoResponseDTO.builder()
                .id(turno.getId())
                .pacienteId(turno.getPaciente().getId())
                .pacienteNombre(turno.getPaciente().getNombre())
                .pacienteApellido(turno.getPaciente().getApellido())
                .odontologoId(turno.getOdontologo().getId())
                .odontologoNombre(turno.getOdontologo().getNombre())
                .odontologoApellido(turno.getOdontologo().getApellido())
                .odontologoMatricula(turno.getOdontologo().getMatricula())
                .recepcionistaId(turno.getRecepcionista().getId())
                .recepcionistaNombre(turno.getRecepcionista().getNombre())
                .recepcionistaApellido(turno.getRecepcionista().getApellido())
                .fechaTurno(turno.getFechaTurno())
                .estado(turno.getEstado())
                .observaciones(turno.getObservaciones())
                .build();
    }
}