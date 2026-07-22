package com.clinica.odontologica.Controller;

import com.clinica.odontologica.Dto.OdontologoRequestDTO;
import com.clinica.odontologica.Dto.OdontologoResponseDTO;
import com.clinica.odontologica.Service.OdontologoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-odontologo")
@Slf4j
public class OdontologoController {

    @Autowired
    private OdontologoService odontologoService;

    @PostMapping
    public ResponseEntity<OdontologoResponseDTO> registrarOdontologo(
            @Valid @RequestBody OdontologoRequestDTO odontologoRequestDTO) {

        log.info("Recibida petición para registrar odontólogo con matrícula: {}",
                odontologoRequestDTO.getMatricula());

        OdontologoResponseDTO odontologoCreado =
                odontologoService.registrarOdontologo(odontologoRequestDTO);

        return new ResponseEntity<>(odontologoCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OdontologoResponseDTO>> listarOdontologos() {

        log.info("Recibida petición para listar todos los odontólogos");

        List<OdontologoResponseDTO> odontologosBuscados =
                odontologoService.listarTodos();

        if (odontologosBuscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(odontologosBuscados, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OdontologoResponseDTO> buscarOdontologoPorId(
            @PathVariable Long id) {

        log.info("Recibida petición para buscar odontólogo con ID: {}", id);

        OdontologoResponseDTO odontologoBuscado =
                odontologoService.buscarOdontologo(id);

        return new ResponseEntity<>(odontologoBuscado, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OdontologoResponseDTO> actualizarOdontologo(
            @PathVariable Long id,
            @Valid @RequestBody OdontologoRequestDTO odontologoRequestDTO) {

        log.info("Recibida petición para actualizar odontólogo con ID: {}", id);

        OdontologoResponseDTO odontologoActualizado =
                odontologoService.actualizarOdontologo(id, odontologoRequestDTO);

        return new ResponseEntity<>(odontologoActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOdontologo(@PathVariable Long id) {

        log.info("Recibida petición para eliminar odontólogo con ID: {}", id);

        odontologoService.eliminarOdontologo(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/buscar")
    public ResponseEntity<OdontologoResponseDTO> buscarOdontologoPorMatricula(
            @RequestParam String matricula) {

        log.info("Recibida petición para buscar odontólogo con matrícula: {}", matricula);

        OdontologoResponseDTO odontologoBuscado =
                odontologoService.buscarPorMatricula(matricula);

        return new ResponseEntity<>(odontologoBuscado, HttpStatus.OK);
    }
}