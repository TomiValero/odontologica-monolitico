package com.clinica.odontologica.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecepcionistaResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String usuario;
}