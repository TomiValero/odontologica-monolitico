package com.clinica.odontologica.Dto;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OdontologoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 10, message = "El nombre debe tener entre 3 y 10 caracteres")
    private String nombre;
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 3, max = 20, message = "El apellido debe tener entre 3 y 10 caracteres")
    private String apellido;
    @NotBlank(message = "Matricula obligatoria")
    private String matricula;
    @NotBlank(message = "El telefono debe ser obligatorio")
    private String telefono;
    @NotBlank(message = "el email debe ser obligatorio")
    private String email;
    @NotBlank(message = "La especialidad debe ser obligatoria")
    private String especialidad;
}
