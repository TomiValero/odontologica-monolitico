package com.clinica.odontologica;

import com.clinica.odontologica.Dto.OdontologoRequestDTO;
import com.clinica.odontologica.Dto.OdontologoResponseDTO;
import com.clinica.odontologica.Entity.Odontologo;
import com.clinica.odontologica.Exception.DuplicatedResourceException;
import com.clinica.odontologica.Exception.InvalidEmailException;
import com.clinica.odontologica.Exception.ResourceNotFoundException;
import com.clinica.odontologica.Repository.OdontologoRepository;
import com.clinica.odontologica.Service.OdontologoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OdontologoServiceTest {

    @Mock
    private OdontologoRepository odontologoRepository;

    @InjectMocks
    private OdontologoService odontologoService;

    private Odontologo odontologo;
    private OdontologoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        odontologo = Odontologo.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .matricula("MAT123")
                .telefono("1122334455")
                .email("juanperez@up.edu.ar")
                .especialidad("Ortodoncia")
                .build();

        requestDTO = OdontologoRequestDTO.builder()
                .nombre("Juan")
                .apellido("Perez")
                .matricula("MAT123")
                .telefono("1122334455")
                .email("juanperez@up.edu.ar")
                .especialidad("Ortodoncia")
                .build();
    }

    @Test
    @DisplayName("Crear odontólogo con datos válidos - debe retornar odontólogo creado")
    void crearOdontologoValido() {

        when(odontologoRepository.existsByMatricula(anyString())).thenReturn(false);
        when(odontologoRepository.existsByEmail(anyString())).thenReturn(false);
        when(odontologoRepository.save(any(Odontologo.class))).thenReturn(odontologo);

        OdontologoResponseDTO resultado = odontologoService.registrarOdontologo(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        assertThat(resultado.getApellido()).isEqualTo("Perez");
        assertThat(resultado.getMatricula()).isEqualTo("MAT123");
        assertThat(resultado.getTelefono()).isEqualTo("1122334455");
        assertThat(resultado.getEmail()).isEqualTo("juanperez@up.edu.ar");
        assertThat(resultado.getEspecialidad()).isEqualTo("Ortodoncia");

        verify(odontologoRepository, times(1)).existsByMatricula("MAT123");
        verify(odontologoRepository, times(1)).existsByEmail("juanperez@up.edu.ar");
        verify(odontologoRepository, times(1)).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Crear odontólogo con matrícula duplicada - debe lanzar DuplicatedResourceException")
    void crearOdontologoConMatriculaDuplicada() {

        when(odontologoRepository.existsByMatricula(anyString())).thenReturn(true);

        assertThatThrownBy(() -> odontologoService.registrarOdontologo(requestDTO))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessageContaining("Ya existe un odontólogo con dicha matrícula");

        verify(odontologoRepository, times(1)).existsByMatricula("MAT123");
        verify(odontologoRepository, never()).existsByEmail(anyString());
        verify(odontologoRepository, never()).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Crear odontólogo con email duplicado - debe lanzar DuplicatedResourceException")
    void crearOdontologoConEmailDuplicado() {

        when(odontologoRepository.existsByMatricula(anyString())).thenReturn(false);
        when(odontologoRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> odontologoService.registrarOdontologo(requestDTO))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessageContaining("Ya existe un odontólogo con dicho email");

        verify(odontologoRepository, times(1)).existsByMatricula("MAT123");
        verify(odontologoRepository, times(1)).existsByEmail("juanperez@up.edu.ar");
        verify(odontologoRepository, never()).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Crear odontólogo con email inválido - debe lanzar InvalidEmailException")
    void crearOdontologoConEmailInvalido() {

        requestDTO.setEmail("juan@");

        assertThatThrownBy(() -> odontologoService.registrarOdontologo(requestDTO))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("no tiene un formato válido");

        verify(odontologoRepository, never()).existsByMatricula(anyString());
        verify(odontologoRepository, never()).existsByEmail(anyString());
        verify(odontologoRepository, never()).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Crear odontólogo sin nombre - debe lanzar IllegalArgumentException")
    void crearOdontologoSinNombre() {

        requestDTO.setNombre("");

        assertThatThrownBy(() -> odontologoService.registrarOdontologo(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre es obligatorio");

        verify(odontologoRepository, never()).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Buscar odontólogo por ID existente - debe retornar odontólogo")
    void buscarOdontologoPorIdExistente() {

        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));

        OdontologoResponseDTO resultado = odontologoService.buscarOdontologo(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        assertThat(resultado.getMatricula()).isEqualTo("MAT123");

        verify(odontologoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar odontólogo por ID inexistente - debe lanzar ResourceNotFoundException")
    void buscarOdontologoPorIdInexistente() {

        when(odontologoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> odontologoService.buscarOdontologo(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el odontólogo con id");

        verify(odontologoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Buscar odontólogo por matrícula existente - debe retornar odontólogo")
    void buscarOdontologoPorMatriculaExistente() {

        when(odontologoRepository.findByMatricula("MAT123"))
                .thenReturn(Optional.of(odontologo));

        OdontologoResponseDTO resultado = odontologoService.buscarPorMatricula("MAT123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getMatricula()).isEqualTo("MAT123");
        assertThat(resultado.getNombre()).isEqualTo("Juan");

        verify(odontologoRepository, times(1)).findByMatricula("MAT123");
    }

    @Test
    @DisplayName("Buscar odontólogo por matrícula inexistente - debe lanzar ResourceNotFoundException")
    void buscarOdontologoPorMatriculaInexistente() {

        when(odontologoRepository.findByMatricula("MAT999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> odontologoService.buscarPorMatricula("MAT999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el odontólogo con matrícula");

        verify(odontologoRepository, times(1)).findByMatricula("MAT999");
    }

    @Test
    @DisplayName("Buscar odontólogo con matrícula vacía - debe lanzar IllegalArgumentException")
    void buscarOdontologoConMatriculaVacia() {

        assertThatThrownBy(() -> odontologoService.buscarPorMatricula(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La matrícula es obligatoria");

        verify(odontologoRepository, never()).findByMatricula(anyString());
    }

    @Test
    @DisplayName("Listar odontólogos - debe retornar lista de odontólogos")
    void listarOdontologos() {

        Odontologo odontologo2 = Odontologo.builder()
                .id(2L)
                .nombre("Ana")
                .apellido("Gomez")
                .matricula("MAT456")
                .telefono("1166778899")
                .email("anagomez@up.edu.ar")
                .especialidad("Endodoncia")
                .build();

        when(odontologoRepository.findAll())
                .thenReturn(Arrays.asList(odontologo, odontologo2));

        List<OdontologoResponseDTO> resultado = odontologoService.listarTodos();

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Ana");

        verify(odontologoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Actualizar odontólogo con datos válidos - debe retornar odontólogo actualizado")
    void actualizarOdontologoValido() {

        OdontologoRequestDTO requestActualizado = OdontologoRequestDTO.builder()
                .nombre("Juan")
                .apellido("Perez")
                .matricula("MAT123")
                .telefono("1199998888")
                .email("juanactualizado@up.edu.ar")
                .especialidad("Implantología")
                .build();

        Odontologo odontologoActualizado = Odontologo.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .matricula("MAT123")
                .telefono("1199998888")
                .email("juanactualizado@up.edu.ar")
                .especialidad("Implantología")
                .build();

        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(odontologoRepository.existsByEmail("juanactualizado@up.edu.ar")).thenReturn(false);
        when(odontologoRepository.save(any(Odontologo.class))).thenReturn(odontologoActualizado);

        OdontologoResponseDTO resultado =
                odontologoService.actualizarOdontologo(1L, requestActualizado);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEmail()).isEqualTo("juanactualizado@up.edu.ar");
        assertThat(resultado.getTelefono()).isEqualTo("1199998888");
        assertThat(resultado.getEspecialidad()).isEqualTo("Implantología");

        verify(odontologoRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).existsByEmail("juanactualizado@up.edu.ar");
        verify(odontologoRepository, never()).existsByMatricula(anyString());
        verify(odontologoRepository, times(1)).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Actualizar odontólogo con matrícula duplicada - debe lanzar DuplicatedResourceException")
    void actualizarOdontologoConMatriculaDuplicada() {

        OdontologoRequestDTO requestActualizado = OdontologoRequestDTO.builder()
                .nombre("Juan")
                .apellido("Perez")
                .matricula("MAT999")
                .telefono("1122334455")
                .email("juanperez@up.edu.ar")
                .especialidad("Ortodoncia")
                .build();

        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(odontologoRepository.existsByMatricula("MAT999")).thenReturn(true);

        assertThatThrownBy(() -> odontologoService.actualizarOdontologo(1L, requestActualizado))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessageContaining("Ya existe un odontólogo con dicha matrícula");

        verify(odontologoRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).existsByMatricula("MAT999");
        verify(odontologoRepository, never()).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Actualizar odontólogo con email duplicado - debe lanzar DuplicatedResourceException")
    void actualizarOdontologoConEmailDuplicado() {

        OdontologoRequestDTO requestActualizado = OdontologoRequestDTO.builder()
                .nombre("Juan")
                .apellido("Perez")
                .matricula("MAT123")
                .telefono("1122334455")
                .email("otro@up.edu.ar")
                .especialidad("Ortodoncia")
                .build();

        when(odontologoRepository.findById(1L)).thenReturn(Optional.of(odontologo));
        when(odontologoRepository.existsByEmail("otro@up.edu.ar")).thenReturn(true);

        assertThatThrownBy(() -> odontologoService.actualizarOdontologo(1L, requestActualizado))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessageContaining("Ya existe un odontólogo con dicho email");

        verify(odontologoRepository, times(1)).findById(1L);
        verify(odontologoRepository, times(1)).existsByEmail("otro@up.edu.ar");
        verify(odontologoRepository, never()).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Actualizar odontólogo inexistente - debe lanzar ResourceNotFoundException")
    void actualizarOdontologoInexistente() {

        when(odontologoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> odontologoService.actualizarOdontologo(99L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el odontólogo con id");

        verify(odontologoRepository, times(1)).findById(99L);
        verify(odontologoRepository, never()).save(any(Odontologo.class));
    }

    @Test
    @DisplayName("Eliminar odontólogo existente - debe eliminar correctamente")
    void eliminarOdontologoExistente() {

        when(odontologoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(odontologoRepository).deleteById(1L);

        odontologoService.eliminarOdontologo(1L);

        verify(odontologoRepository, times(1)).existsById(1L);
        verify(odontologoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar odontólogo inexistente - debe lanzar ResourceNotFoundException")
    void eliminarOdontologoInexistente() {

        when(odontologoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> odontologoService.eliminarOdontologo(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el odontólogo con id");

        verify(odontologoRepository, times(1)).existsById(99L);
        verify(odontologoRepository, never()).deleteById(99L);
    }
}