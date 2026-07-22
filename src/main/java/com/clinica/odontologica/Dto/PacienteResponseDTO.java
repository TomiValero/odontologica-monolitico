package com.clinica.odontologica.Dto;

import com.clinica.odontologica.Entity.Domicilio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String cedula;
    private LocalDate fechaIngreso;
    private String email;
    private DomicilioResponseDTO  domicilio;
}
