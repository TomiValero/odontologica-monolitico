package com.clinica.odontologica.Controller;

import com.clinica.odontologica.Dto.PacienteRequestDTO;
import com.clinica.odontologica.Dto.PacienteResponseDTO;
import com.clinica.odontologica.Service.PacienteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-paciente")
@Slf4j
public class PacienteController {
    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> registrarPaciente(
            @Valid @RequestBody PacienteRequestDTO pacienteRequestDTO) {

        log.info("Recibida petición para registrar paciente con email: {}",
                pacienteRequestDTO.getEmail());

        PacienteResponseDTO pacienteCreado =
                pacienteService.registrarPaciente(pacienteRequestDTO);

        return new ResponseEntity<>(pacienteCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarPacientes() {

        log.info("Recibida petición para listar todos los pacientes");

        List<PacienteResponseDTO> pacientesBuscados =
                pacienteService.listarTodos();

        if (pacientesBuscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(pacientesBuscados, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPacientePorId(
            @PathVariable Long id) {

        log.info("Recibida petición para buscar paciente con ID: {}", id);

        PacienteResponseDTO pacienteBuscado =
                pacienteService.buscarPaciente(id);

        return new ResponseEntity<>(pacienteBuscado, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> actualizarPaciente(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO pacienteRequestDTO) {

        log.info("Recibida petición para actualizar paciente con ID: {}", id);

        PacienteResponseDTO pacienteActualizado =
                pacienteService.actualizarPaciente(id, pacienteRequestDTO);

        return new ResponseEntity<>(pacienteActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {

        log.info("Recibida petición para eliminar paciente con ID: {}", id);

        pacienteService.eliminarPaciente(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/buscar-cedula")
    public ResponseEntity<PacienteResponseDTO> buscarPacientePorCedula(
            @RequestParam String cedula) {

        log.info("Recibida petición para buscar paciente con cédula: {}", cedula);

        PacienteResponseDTO pacienteBuscado =
                pacienteService.buscarPorCedula(cedula);

        return new ResponseEntity<>(pacienteBuscado, HttpStatus.OK);

    }

    @GetMapping("/buscar-email/{email}")
    public ResponseEntity<PacienteResponseDTO> buscarPacientePorEmail(
            @PathVariable String email) {

        log.info("Recibida petición para buscar paciente con email: {}", email);

        PacienteResponseDTO pacienteBuscado =
                pacienteService.buscarPorEmail(email);

        return new ResponseEntity<>(pacienteBuscado, HttpStatus.OK);
    }
}
