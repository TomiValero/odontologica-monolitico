package com.clinica.odontologica.Dto;

import com.clinica.odontologica.Entity.Domicilio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class PacienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min=3,max=10, message = "El nombre debe tener entre 3 y 10 caracteres")
    private String nombre;
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min=3,max=20, message = "El apellido debe tener entre 3 y 10 caracteres")
    private String apellido;
    @NotBlank(message = "Cedula obligatoria")
    @Positive(message = "valores sin -")
    private String cedula;
    private LocalDate fechaIngreso;
    @NotBlank(message = "el email debe ser obligatorio")
    private String email;
    @NotNull(message = "Domicilio Obligatorio")
    @Valid
    private DomicilioRequestDTO  domicilio;
}
