package com.clinica.odontologica.Dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoRequestDTO {

    @NotNull(message = "El paciente es obligatorio")
    private Long pacienteId;
    @NotNull(message = "El odontólogo es obligatorio")
    private Long odontologoId;
    @NotNull(message = "El recepcionista es obligatorio")
    private Long recepcionistaId;
    @NotNull(message = "La fecha del turno es obligatoria")
    @Future(message = "La fecha del turno no puede estar en el pasado")
    private LocalDateTime fechaTurno;
    private String estado;
    private String observaciones;
}
