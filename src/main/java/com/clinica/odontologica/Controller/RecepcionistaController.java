package com.clinica.odontologica.Controller;

import com.clinica.odontologica.Dto.RecepcionistaRequestDTO;
import com.clinica.odontologica.Dto.RecepcionistaResponseDTO;
import com.clinica.odontologica.Service.RecepcionistaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-recepcionista")
@Slf4j
public class RecepcionistaController {

    @Autowired
    private RecepcionistaService recepcionistaService;

    @PostMapping
    public ResponseEntity<RecepcionistaResponseDTO> registrarRecepcionista(
            @Valid @RequestBody RecepcionistaRequestDTO recepcionistaRequestDTO) {

        log.info("Recibida petición para registrar recepcionista con usuario: {}",
                recepcionistaRequestDTO.getUsuario());

        RecepcionistaResponseDTO recepcionistaCreado =
                recepcionistaService.registrarRecepcionista(recepcionistaRequestDTO);

        return new ResponseEntity<>(recepcionistaCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RecepcionistaResponseDTO>> listarRecepcionistas() {

        log.info("Recibida petición para listar todos los recepcionistas");

        List<RecepcionistaResponseDTO> recepcionistasBuscados =
                recepcionistaService.listarTodos();

        if (recepcionistasBuscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(recepcionistasBuscados, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecepcionistaResponseDTO> buscarRecepcionistaPorId(
            @PathVariable Long id) {

        log.info("Recibida petición para buscar recepcionista con ID: {}", id);

        RecepcionistaResponseDTO recepcionistaBuscado =
                recepcionistaService.buscarRecepcionista(id);

        return new ResponseEntity<>(recepcionistaBuscado, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecepcionistaResponseDTO> actualizarRecepcionista(
            @PathVariable Long id,
            @Valid @RequestBody RecepcionistaRequestDTO recepcionistaRequestDTO) {

        log.info("Recibida petición para actualizar recepcionista con ID: {}", id);

        RecepcionistaResponseDTO recepcionistaActualizado =
                recepcionistaService.actualizarRecepcionista(id, recepcionistaRequestDTO);

        return new ResponseEntity<>(recepcionistaActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRecepcionista(@PathVariable Long id) {

        log.info("Recibida petición para eliminar recepcionista con ID: {}", id);

        recepcionistaService.eliminarRecepcionista(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}