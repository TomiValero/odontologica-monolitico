package com.clinica.odontologica.Service;

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
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Builder
@Slf4j
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional
    public PacienteResponseDTO registrarPaciente(
            PacienteRequestDTO pacienteRequestDTO
    ) {

        log.info("Creando nuevo paciente con email: {}", pacienteRequestDTO.getEmail());

        validarDatosPaciente(pacienteRequestDTO);
        validarEmail(pacienteRequestDTO.getEmail());

        if (pacienteRepository.existsByEmail(pacienteRequestDTO.getEmail())) {
            log.warn("Intento de crear paciente con email duplicado: {}", pacienteRequestDTO.getEmail());
            throw new DuplicatedResourceException("Ya existe un paciente con dicho email: " + pacienteRequestDTO.getEmail());
        }

        if (pacienteRepository.existsByCedula(pacienteRequestDTO.getCedula())) {
            log.warn("Intento de crear paciente con cédula duplicada: {}", pacienteRequestDTO.getCedula());
            throw new DuplicatedResourceException("Ya existe un paciente con dicha cédula: " + pacienteRequestDTO.getCedula());
        }

        Domicilio domicilio = convertirDomicilioRequestDtoAEntidad(pacienteRequestDTO.getDomicilio());
        Paciente paciente = convertirRequestDtoAEntidad(pacienteRequestDTO, domicilio);

        Paciente pacienteCreado = pacienteRepository.save(paciente);

        log.info("Paciente creado correctamente con ID: {}", pacienteCreado.getId());

        return convertirEntidadADtoResponse(pacienteCreado);
    }

    @Transactional
    public PacienteResponseDTO actualizarPaciente(
            Long id,
            PacienteRequestDTO pacienteRequestDTO
    ) {

        log.info("Actualizando paciente con ID: {}", id);

        validarDatosPaciente(pacienteRequestDTO);
        validarEmail(pacienteRequestDTO.getEmail());

        Paciente pacienteBuscado = pacienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Paciente no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("No se encontró el paciente con id: " + id);
                });

        if (!pacienteBuscado.getEmail().equals(pacienteRequestDTO.getEmail())
                && pacienteRepository.existsByEmail(pacienteRequestDTO.getEmail())) {

            log.warn("Intento de actualizar paciente con email duplicado: {}", pacienteRequestDTO.getEmail());
            throw new DuplicatedResourceException("Ya existe un paciente con dicho email: " + pacienteRequestDTO.getEmail());
        }

        if (!pacienteBuscado.getCedula().equals(pacienteRequestDTO.getCedula())
                && pacienteRepository.existsByCedula(pacienteRequestDTO.getCedula())) {

            log.warn("Intento de actualizar paciente con cédula duplicada: {}", pacienteRequestDTO.getCedula());
            throw new DuplicatedResourceException("Ya existe un paciente con dicha cédula: " + pacienteRequestDTO.getCedula());
        }

        Domicilio domicilioExistente = pacienteBuscado.getDomicilio();

        domicilioExistente.setCalle(pacienteRequestDTO.getDomicilio().getCalle());
        domicilioExistente.setLocalidad(pacienteRequestDTO.getDomicilio().getLocalidad());
        domicilioExistente.setProvincia(pacienteRequestDTO.getDomicilio().getProvincia());

        pacienteBuscado.setNombre(pacienteRequestDTO.getNombre());
        pacienteBuscado.setApellido(pacienteRequestDTO.getApellido());
        pacienteBuscado.setCedula(pacienteRequestDTO.getCedula());
        pacienteBuscado.setFechaIngreso(obtenerFechaIngreso(pacienteRequestDTO));
        pacienteBuscado.setEmail(pacienteRequestDTO.getEmail());
        pacienteBuscado.setDomicilio(domicilioExistente);

        Paciente pacienteActualizado = pacienteRepository.save(pacienteBuscado);

        log.info("Paciente actualizado correctamente con ID: {}", id);

        return convertirEntidadADtoResponse(pacienteActualizado);
    }

    @Transactional
    public void eliminarPaciente(Long id) {

        log.info("Eliminando paciente con ID: {}", id);

        if (!pacienteRepository.existsById(id)) {
            log.warn("Intento de eliminar paciente inexistente con ID: {}", id);
            throw new ResourceNotFoundException("No se encontró el paciente con id: " + id);
        }

        pacienteRepository.deleteById(id);

        log.info("Paciente eliminado correctamente con ID: {}", id);
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPaciente(Long id) {

        Paciente pacienteBuscado = pacienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Paciente no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("No se encontró el paciente con id: " + id);
                });

        return convertirEntidadADtoResponse(pacienteBuscado);
    }


    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPorCedula(String cedula) {

        log.info("Buscando paciente por cédula: {}", cedula);

        if (cedula == null || cedula.trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula es obligatoria");
        }

        Paciente pacienteBuscado = pacienteRepository.findByCedula(cedula)
                .orElseThrow(() -> {
                    log.warn("Paciente no encontrado con cédula: {}", cedula);
                    return new ResourceNotFoundException("No se encontró el paciente con cédula: " + cedula);
                });

        return convertirEntidadADtoResponse(pacienteBuscado);
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPorEmail(String email) {

        log.info("Buscando paciente por email: {}", email);

        validarEmail(email);

        Paciente pacienteBuscado = pacienteRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Paciente no encontrado con email: {}", email);
                    return new ResourceNotFoundException("No se encontró el paciente con email: " + email);
                });

        return convertirEntidadADtoResponse(pacienteBuscado);
    }

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> listarTodos() {

        log.info("Listando todos los pacientes");

        List<Paciente> pacienteList = pacienteRepository.findAll();

        List<PacienteResponseDTO> pacienteDTOList = new ArrayList<>();

        for (Paciente paciente : pacienteList) {
            PacienteResponseDTO dto = convertirEntidadADtoResponse(paciente);
            pacienteDTOList.add(dto);
        }

        return pacienteDTOList;
    }

    private Paciente convertirRequestDtoAEntidad(
            PacienteRequestDTO pacienteRequestDTO,
            Domicilio domicilio) {

        return Paciente.builder()
                .nombre(pacienteRequestDTO.getNombre())
                .apellido(pacienteRequestDTO.getApellido())
                .cedula(pacienteRequestDTO.getCedula())
                .fechaIngreso(obtenerFechaIngreso(pacienteRequestDTO))
                .email(pacienteRequestDTO.getEmail())
                .domicilio(domicilio)
                .build();
    }

    private PacienteResponseDTO convertirEntidadADtoResponse(Paciente paciente) {

        return PacienteResponseDTO.builder()
                .id(paciente.getId())
                .nombre(paciente.getNombre())
                .apellido(paciente.getApellido())
                .cedula(paciente.getCedula())
                .fechaIngreso(paciente.getFechaIngreso())
                .email(paciente.getEmail())
                .domicilio(convertirDomicilioEntidadADtoResponse(paciente.getDomicilio()))
                .build();
    }

    private LocalDate obtenerFechaIngreso(PacienteRequestDTO pacienteRequestDTO) {

        if (pacienteRequestDTO.getFechaIngreso() == null) {
            return LocalDate.now();
        }

        return pacienteRequestDTO.getFechaIngreso();
    }

    private void validarDatosPaciente(PacienteRequestDTO pacienteRequestDTO) {

        if (pacienteRequestDTO.getNombre() == null || pacienteRequestDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (pacienteRequestDTO.getApellido() == null || pacienteRequestDTO.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }

        if (pacienteRequestDTO.getCedula() == null || pacienteRequestDTO.getCedula().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula es obligatoria");
        }

        if (pacienteRequestDTO.getEmail() == null || pacienteRequestDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (pacienteRequestDTO.getDomicilio() == null) {
            throw new IllegalArgumentException("El domicilio es obligatorio");
        }

        validarDomicilioDatosObligatorios(pacienteRequestDTO.getDomicilio());
    }

    private void validarDomicilioDatosObligatorios(DomicilioRequestDTO domicilioRequestDTO) {

        if (domicilioRequestDTO == null) {
            throw new IllegalArgumentException("El domicilio es obligatorio");
        }

        if (domicilioRequestDTO.getCalle() == null || domicilioRequestDTO.getCalle().trim().isEmpty()) {
            throw new IllegalArgumentException("La calle es obligatoria");
        }

        if (domicilioRequestDTO.getLocalidad() == null || domicilioRequestDTO.getLocalidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La localidad es obligatoria");
        }

        if (domicilioRequestDTO.getProvincia() == null || domicilioRequestDTO.getProvincia().trim().isEmpty()) {
            throw new IllegalArgumentException("La provincia es obligatoria");
        }
    }

    private void validarEmail(String email) {

        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (email == null || !email.matches(regexEmail)) {
            log.warn("Email inválido recibido: {}", email);
            throw new InvalidEmailException("El email '" + email + "' no tiene un formato válido");
        }
    }

    public Domicilio convertirDomicilioRequestDtoAEntidad(DomicilioRequestDTO domicilioRequestDTO) {

        return Domicilio.builder()
                .calle(domicilioRequestDTO.getCalle())
                .localidad(domicilioRequestDTO.getLocalidad())
                .provincia(domicilioRequestDTO.getProvincia())
                .build();
    }

    public DomicilioResponseDTO convertirDomicilioEntidadADtoResponse(Domicilio domicilio) {

        if (domicilio == null) {
            return null;
        }

        return DomicilioResponseDTO.builder()
                .id(domicilio.getId())
                .calle(domicilio.getCalle())
                .localidad(domicilio.getLocalidad())
                .provincia(domicilio.getProvincia())
                .build();
    }
}