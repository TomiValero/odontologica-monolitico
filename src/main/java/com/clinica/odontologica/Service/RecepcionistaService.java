package com.clinica.odontologica.Service;

import com.clinica.odontologica.Dto.PacienteResponseDTO;
import com.clinica.odontologica.Dto.RecepcionistaRequestDTO;
import com.clinica.odontologica.Dto.RecepcionistaResponseDTO;
import com.clinica.odontologica.Entity.Paciente;
import com.clinica.odontologica.Entity.Recepcionista;
import com.clinica.odontologica.Exception.DuplicatedRecepcionistaException;
import com.clinica.odontologica.Exception.InvalidEmailException;
import com.clinica.odontologica.Exception.RecepcionistaNotFoundException;
import com.clinica.odontologica.Exception.ResourceNotFoundException;
import com.clinica.odontologica.Repository.RecepcionistaRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Builder
@Slf4j
public class RecepcionistaService {

    @Autowired
    private RecepcionistaRepository recepcionistaRepository;

    @Transactional
    public RecepcionistaResponseDTO registrarRecepcionista(
            RecepcionistaRequestDTO recepcionistaRequestDTO) {

        log.info("Registrando recepcionista con email: {}", recepcionistaRequestDTO.getEmail());

        validarDatosObligatorios(recepcionistaRequestDTO);
        validarEmail(recepcionistaRequestDTO.getEmail());

        if (recepcionistaRepository.existsByEmail(recepcionistaRequestDTO.getEmail())) {
            log.warn("Intento de crear recepcionista con email duplicado: {}",
                    recepcionistaRequestDTO.getEmail());

            throw new DuplicatedRecepcionistaException(
                    "Ya existe un recepcionista con dicho email: " + recepcionistaRequestDTO.getEmail()
            );
        }

        if (recepcionistaRepository.existsByUsuario(recepcionistaRequestDTO.getUsuario())) {
            log.warn("Intento de crear recepcionista con usuario duplicado: {}",
                    recepcionistaRequestDTO.getUsuario());

            throw new DuplicatedRecepcionistaException(
                    "Ya existe un recepcionista con dicho usuario: " + recepcionistaRequestDTO.getUsuario()
            );
        }

        Recepcionista recepcionista = convertirRequestDtoAEntidad(recepcionistaRequestDTO);

        Recepcionista recepcionistaCreado = recepcionistaRepository.save(recepcionista);

        log.info("Recepcionista creado correctamente con ID: {}", recepcionistaCreado.getId());

        return convertirEntidadADtoResponse(recepcionistaCreado);
    }

    @Transactional(readOnly = true)
    public RecepcionistaResponseDTO buscarRecepcionista(Long id) {

        log.info("Buscando recepcionista con ID: {}", id);

        Recepcionista recepcionistaBuscado = recepcionistaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Recepcionista no encontrado con ID: {}", id);
                    return new RecepcionistaNotFoundException(
                            "No se encontró el recepcionista con id: " + id
                    );
                });

        return convertirEntidadADtoResponse(recepcionistaBuscado);
    }

    @Transactional(readOnly = true)
    public RecepcionistaResponseDTO buscarPorUsuario(String usuario) {

        log.info("Buscando recepcionista por usuario: {}", usuario);

        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        Recepcionista recepcionistaBuscado = recepcionistaRepository.findByUsuario(usuario)
                .orElseThrow(() -> {
                    log.warn("Recepcionista no encontrado con usuario: {}", usuario);
                    return new ResourceNotFoundException("No se encontró el recepcionista con usuario: " + usuario);
                });

        return convertirEntidadADtoResponse(recepcionistaBuscado);
    }

    @Transactional(readOnly = true)
    public RecepcionistaResponseDTO buscarPorEmail(String email) {

        log.info("Buscando recepcionista por email: {}", email);

        validarEmail(email);

        Recepcionista recepcionistaBuscado = recepcionistaRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Recepcionista no encontrado con email: {}", email);
                    return new ResourceNotFoundException("No se encontró el recepcionista con email: " + email);
                });

        return convertirEntidadADtoResponse(recepcionistaBuscado);
    }

    @Transactional(readOnly = true)
    public List<RecepcionistaResponseDTO> listarTodos() {

        log.info("Listando todos los recepcionistas");

        List<Recepcionista> recepcionistaList = recepcionistaRepository.findAll();

        List<RecepcionistaResponseDTO> recepcionistaDTOList = new ArrayList<>();

        for (Recepcionista recepcionista : recepcionistaList) {
            RecepcionistaResponseDTO dto = convertirEntidadADtoResponse(recepcionista);
            recepcionistaDTOList.add(dto);
        }

        return recepcionistaDTOList;
    }

    @Transactional
    public RecepcionistaResponseDTO actualizarRecepcionista(Long id, RecepcionistaRequestDTO recepcionistaRequestDTO) {

        log.info("Actualizando recepcionista con ID: {}", id);

        validarDatosObligatorios(recepcionistaRequestDTO);
        validarEmail(recepcionistaRequestDTO.getEmail());

        Recepcionista recepcionistaBuscado = recepcionistaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Recepcionista no encontrado con ID: {}", id);
                    return new RecepcionistaNotFoundException(
                            "No se encontró el recepcionista con id: " + id
                    );
                });

        if (!recepcionistaBuscado.getEmail().equals(recepcionistaRequestDTO.getEmail())
                && recepcionistaRepository.existsByEmail(recepcionistaRequestDTO.getEmail())) {

            log.warn("Intento de actualizar recepcionista con email duplicado: {}", recepcionistaRequestDTO.getEmail());

            throw new DuplicatedRecepcionistaException("Ya existe un recepcionista con dicho email: " + recepcionistaRequestDTO.getEmail());
        }

        if (!recepcionistaBuscado.getUsuario().equals(recepcionistaRequestDTO.getUsuario())
                && recepcionistaRepository.existsByUsuario(recepcionistaRequestDTO.getUsuario())) {

            log.warn("Intento de actualizar recepcionista con usuario duplicado: {}",
                    recepcionistaRequestDTO.getUsuario());

            throw new DuplicatedRecepcionistaException(
                    "Ya existe un recepcionista con dicho usuario: " + recepcionistaRequestDTO.getUsuario()
            );
        }

        recepcionistaBuscado.setNombre(recepcionistaRequestDTO.getNombre());
        recepcionistaBuscado.setApellido(recepcionistaRequestDTO.getApellido());
        recepcionistaBuscado.setEmail(recepcionistaRequestDTO.getEmail());
        recepcionistaBuscado.setTelefono(recepcionistaRequestDTO.getTelefono());
        recepcionistaBuscado.setUsuario(recepcionistaRequestDTO.getUsuario());

        Recepcionista recepcionistaActualizado = recepcionistaRepository.save(recepcionistaBuscado);

        log.info("Recepcionista actualizado correctamente con ID: {}", id);

        return convertirEntidadADtoResponse(recepcionistaActualizado);
    }

    @Transactional
    public void eliminarRecepcionista(Long id) {

        log.info("Eliminando recepcionista con ID: {}", id);

        if (!recepcionistaRepository.existsById(id)) {
            log.warn("Intento de eliminar recepcionista inexistente con ID: {}", id);
            throw new RecepcionistaNotFoundException(
                    "No se encontró el recepcionista con id: " + id
            );
        }

        recepcionistaRepository.deleteById(id);

        log.info("Recepcionista eliminado correctamente con ID: {}", id);
    }

    private void validarDatosObligatorios(RecepcionistaRequestDTO recepcionistaRequestDTO) {

        if (recepcionistaRequestDTO.getNombre() == null ||
                recepcionistaRequestDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (recepcionistaRequestDTO.getApellido() == null ||
                recepcionistaRequestDTO.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }

        if (recepcionistaRequestDTO.getEmail() == null ||
                recepcionistaRequestDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (recepcionistaRequestDTO.getTelefono() == null ||
                recepcionistaRequestDTO.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }

        if (recepcionistaRequestDTO.getUsuario() == null ||
                recepcionistaRequestDTO.getUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
    }

    private void validarEmail(String email) {

        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (email == null || !email.matches(regexEmail)) {
            log.warn("Email inválido recibido: {}", email);
            throw new InvalidEmailException(
                    "El email '" + email + "' no tiene un formato válido"
            );
        }
    }

    private Recepcionista convertirRequestDtoAEntidad(
            RecepcionistaRequestDTO recepcionistaRequestDTO) {

        return Recepcionista.builder()
                .nombre(recepcionistaRequestDTO.getNombre())
                .apellido(recepcionistaRequestDTO.getApellido())
                .email(recepcionistaRequestDTO.getEmail())
                .telefono(recepcionistaRequestDTO.getTelefono())
                .usuario(recepcionistaRequestDTO.getUsuario())
                .build();
    }

    private RecepcionistaResponseDTO convertirEntidadADtoResponse(
            Recepcionista recepcionista) {

        return RecepcionistaResponseDTO.builder()
                .id(recepcionista.getId())
                .nombre(recepcionista.getNombre())
                .apellido(recepcionista.getApellido())
                .email(recepcionista.getEmail())
                .telefono(recepcionista.getTelefono())
                .usuario(recepcionista.getUsuario())
                .build();
    }
}