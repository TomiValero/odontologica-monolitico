package com.clinica.odontologica.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecepcionistaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min=3,max=10, message = "El nombre debe tener entre 3 y 10 caracteres")
    private String nombre;
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min=3,max=20, message = "El apellido debe tener entre 3 y 10 caracteres")
    private String apellido;
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;
}