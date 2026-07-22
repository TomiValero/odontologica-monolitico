package com.clinica.odontologica.Dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OdontologoResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String matricula;
    private String telefono;
    private String email;
    private String especialidad;
}
