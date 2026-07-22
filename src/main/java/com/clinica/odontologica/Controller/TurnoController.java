package com.clinica.odontologica.Controller;

import com.clinica.odontologica.Dto.TurnoRequestDTO;
import com.clinica.odontologica.Dto.TurnoResponseDTO;
import com.clinica.odontologica.Service.TurnoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-turno")
@Slf4j
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping
    public ResponseEntity<TurnoResponseDTO> registrarTurno(
            @Valid @RequestBody TurnoRequestDTO turnoRequestDTO) {

        log.info("Recibida petición para registrar turno");

        TurnoResponseDTO turnoCreado = turnoService.registrarTurno(turnoRequestDTO);

        return new ResponseEntity<>(turnoCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TurnoResponseDTO>> listarTurnos() {

        log.info("Recibida petición para listar turnos");

        List<TurnoResponseDTO> turnosBuscados = turnoService.listarTodos();

        if (turnosBuscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(turnosBuscados, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> buscarTurnoPorId(@PathVariable Long id) {

        log.info("Recibida petición para buscar turno con ID: {}", id);

        TurnoResponseDTO turnoBuscado = turnoService.buscarTurno(id);

        return new ResponseEntity<>(turnoBuscado, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> actualizarTurno(
            @PathVariable Long id,
            @Valid @RequestBody TurnoRequestDTO turnoRequestDTO) {

        log.info("Recibida petición para actualizar turno con ID: {}", id);

        TurnoResponseDTO turnoActualizado = turnoService.actualizarTurno(id, turnoRequestDTO);

        return new ResponseEntity<>(turnoActualizado, HttpStatus.OK);
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<TurnoResponseDTO> cancelarTurno(
            @PathVariable Long id,
            @RequestParam Long recepcionistaId) {

        log.info("Recibida petición para cancelar turno con ID: {} por recepcionista ID: {}",
                id, recepcionistaId);

        TurnoResponseDTO turnoCancelado = turnoService.cancelarTurno(id, recepcionistaId);

        return new ResponseEntity<>(turnoCancelado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(
            @PathVariable Long id,
            @RequestParam Long recepcionistaId) {

        log.info("Recibida petición para eliminar turno con ID: {} por recepcionista ID: {}",
                id, recepcionistaId);

        turnoService.eliminarTurno(id, recepcionistaId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/odontologo/{odontologoId}")
    public ResponseEntity<List<TurnoResponseDTO>> buscarTurnosPorOdontologo(
            @PathVariable Long odontologoId) {

        log.info("Recibida petición para buscar turnos por odontólogo ID: {}", odontologoId);

        List<TurnoResponseDTO> turnosBuscados =
                turnoService.buscarTurnosPorOdontologo(odontologoId);

        if (turnosBuscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(turnosBuscados, HttpStatus.OK);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<TurnoResponseDTO>> buscarTurnosPorPaciente(
            @PathVariable Long pacienteId) {

        log.info("Recibida petición para buscar turnos por paciente ID: {}", pacienteId);

        List<TurnoResponseDTO> turnosBuscados =
                turnoService.buscarTurnosPorPaciente(pacienteId);

        if (turnosBuscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(turnosBuscados, HttpStatus.OK);
    }

    @GetMapping("/recepcionista/{recepcionistaID}")
    public ResponseEntity<List<TurnoResponseDTO>> buscarTurnosPorRecepcionista(
            @PathVariable Long recepcionistaID) {

        log.info("Recibida petición para buscar turnos por recepcionista ID: {}", recepcionistaID);

        List<TurnoResponseDTO> turnosBuscados =
                turnoService.buscarTurnosPorRecepcionista(recepcionistaID);

        if (turnosBuscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(turnosBuscados, HttpStatus.OK);
    }
}