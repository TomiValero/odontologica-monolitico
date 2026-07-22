package com.clinica.odontologica.Service;

import com.clinica.odontologica.Dto.OdontologoRequestDTO;
import com.clinica.odontologica.Dto.OdontologoResponseDTO;
import com.clinica.odontologica.Entity.Odontologo;
import com.clinica.odontologica.Exception.DuplicatedResourceException;
import com.clinica.odontologica.Exception.InvalidEmailException;
import com.clinica.odontologica.Exception.ResourceNotFoundException;
import com.clinica.odontologica.Repository.OdontologoRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Builder
@Slf4j
public class OdontologoService {

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Transactional
    public OdontologoResponseDTO registrarOdontologo(OdontologoRequestDTO odontologoRequestDTO) {

        log.info("Creando un nuevo odontólogo con matrícula: {}", odontologoRequestDTO.getMatricula());

        validarDatosObligatorios(odontologoRequestDTO);
        validarEmail(odontologoRequestDTO.getEmail());

        if (odontologoRepository.existsByMatricula(odontologoRequestDTO.getMatricula())) {
            log.warn("Intento de crear odontólogo con matrícula duplicada: {}", odontologoRequestDTO.getMatricula());
            throw new DuplicatedResourceException(
                    "Ya existe un odontólogo con dicha matrícula: " + odontologoRequestDTO.getMatricula()
            );
        }

        if (odontologoRepository.existsByEmail(odontologoRequestDTO.getEmail())) {
            log.warn("Intento de crear odontólogo con email duplicado: {}", odontologoRequestDTO.getEmail());
            throw new DuplicatedResourceException(
                    "Ya existe un odontólogo con dicho email: " + odontologoRequestDTO.getEmail()
            );
        }

        Odontologo odontologo = convertirRequestDtoAEntidad(odontologoRequestDTO);

        Odontologo odontologoCreado = odontologoRepository.save(odontologo);

        log.info("Odontólogo creado correctamente con ID: {}", odontologoCreado.getId());

        return convertirEntidadADtoResponse(odontologoCreado);
    }

    @Transactional
    public OdontologoResponseDTO actualizarOdontologo(Long id, OdontologoRequestDTO odontologoRequestDTO) {

        log.info("Actualizando odontólogo con ID: {}", id);

        validarDatosObligatorios(odontologoRequestDTO);
        validarEmail(odontologoRequestDTO.getEmail());

        Odontologo odontologoBuscado = odontologoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Odontólogo no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("No se encontró el odontólogo con id: " + id);
                });

        if (!odontologoBuscado.getMatricula().equals(odontologoRequestDTO.getMatricula())
                && odontologoRepository.existsByMatricula(odontologoRequestDTO.getMatricula())) {

            log.warn("Intento de actualizar odontólogo con matrícula duplicada: {}", odontologoRequestDTO.getMatricula());
            throw new DuplicatedResourceException(
                    "Ya existe un odontólogo con dicha matrícula: " + odontologoRequestDTO.getMatricula()
            );
        }

        if (!odontologoBuscado.getEmail().equals(odontologoRequestDTO.getEmail())
                && odontologoRepository.existsByEmail(odontologoRequestDTO.getEmail())) {

            log.warn("Intento de actualizar odontólogo con email duplicado: {}", odontologoRequestDTO.getEmail());
            throw new DuplicatedResourceException(
                    "Ya existe un odontólogo con dicho email: " + odontologoRequestDTO.getEmail()
            );
        }

        odontologoBuscado.setNombre(odontologoRequestDTO.getNombre());
        odontologoBuscado.setApellido(odontologoRequestDTO.getApellido());
        odontologoBuscado.setMatricula(odontologoRequestDTO.getMatricula());
        odontologoBuscado.setTelefono(odontologoRequestDTO.getTelefono());
        odontologoBuscado.setEmail(odontologoRequestDTO.getEmail());
        odontologoBuscado.setEspecialidad(odontologoRequestDTO.getEspecialidad());

        Odontologo odontologoActualizado = odontologoRepository.save(odontologoBuscado);

        log.info("Odontólogo actualizado correctamente con ID: {}", id);

        return convertirEntidadADtoResponse(odontologoActualizado);
    }

    @Transactional
    public void eliminarOdontologo(Long id) {

        log.info("Eliminando odontólogo con ID: {}", id);

        if (!odontologoRepository.existsById(id)) {
            log.warn("Intento de eliminar un odontólogo inexistente con ID: {}", id);
            throw new ResourceNotFoundException("No se encontró el odontólogo con id: " + id);
        }

        odontologoRepository.deleteById(id);

        log.info("Odontólogo eliminado correctamente con ID: {}", id);
    }

    @Transactional(readOnly = true)
    public OdontologoResponseDTO buscarOdontologo(Long id) {

        log.info("Buscando odontólogo por ID: {}", id);

        Odontologo odontologoBuscado = odontologoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Odontólogo no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("No se encontró el odontólogo con id: " + id);
                });

        log.info("Odontólogo encontrado: {} {}", odontologoBuscado.getNombre(), odontologoBuscado.getApellido());

        return convertirEntidadADtoResponse(odontologoBuscado);
    }

    @Transactional(readOnly = true)
    public OdontologoResponseDTO buscarPorMatricula(String matricula) {

        log.info("Buscando odontólogo por matrícula: {}", matricula);

        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("La matrícula es obligatoria");
        }

        Odontologo odontologoBuscado = odontologoRepository.findByMatricula(matricula)
                .orElseThrow(() -> {
                    log.warn("Odontólogo no encontrado con matrícula: {}", matricula);
                    return new ResourceNotFoundException("No se encontró el odontólogo con matrícula: " + matricula);
                });

        log.info("Odontólogo encontrado con matrícula: {}", matricula);

        return convertirEntidadADtoResponse(odontologoBuscado);
    }

    @Transactional(readOnly = true)
    public List<OdontologoResponseDTO> listarTodos() {

        log.info("Listando todos los odontólogos");

        List<Odontologo> odontologoList = odontologoRepository.findAll();

        List<OdontologoResponseDTO> odontologoDTOList = new ArrayList<>();

        for (Odontologo odontologo : odontologoList) {
            OdontologoResponseDTO dto = convertirEntidadADtoResponse(odontologo);
            odontologoDTOList.add(dto);
        }

        return odontologoDTOList;
    }

    private void validarDatosObligatorios(OdontologoRequestDTO odontologoRequestDTO) {

        if (odontologoRequestDTO.getNombre() == null || odontologoRequestDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (odontologoRequestDTO.getApellido() == null || odontologoRequestDTO.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }

        if (odontologoRequestDTO.getMatricula() == null || odontologoRequestDTO.getMatricula().trim().isEmpty()) {
            throw new IllegalArgumentException("La matrícula es obligatoria");
        }

        if (odontologoRequestDTO.getEmail() == null || odontologoRequestDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (odontologoRequestDTO.getEspecialidad() == null || odontologoRequestDTO.getEspecialidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad es obligatoria");
        }
    }

    private void validarEmail(String email) {

        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!email.matches(regexEmail)) {
            log.warn("Email inválido recibido: {}", email);
            throw new InvalidEmailException("El email '" + email + "' no tiene un formato válido");
        }
    }

    private Odontologo convertirRequestDtoAEntidad(OdontologoRequestDTO odontologoRequestDTO) {

        return Odontologo.builder()
                .nombre(odontologoRequestDTO.getNombre())
                .apellido(odontologoRequestDTO.getApellido())
                .matricula(odontologoRequestDTO.getMatricula())
                .telefono(odontologoRequestDTO.getTelefono())
                .email(odontologoRequestDTO.getEmail())
                .especialidad(odontologoRequestDTO.getEspecialidad())
                .build();
    }

    private OdontologoResponseDTO convertirEntidadADtoResponse(Odontologo odontologo) {

        return OdontologoResponseDTO.builder()
                .id(odontologo.getId())
                .nombre(odontologo.getNombre())
                .apellido(odontologo.getApellido())
                .matricula(odontologo.getMatricula())
                .telefono(odontologo.getTelefono())
                .email(odontologo.getEmail())
                .especialidad(odontologo.getEspecialidad())
                .build();
    }
}