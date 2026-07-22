package com.clinica.odontologica.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private String pacienteApellido;
    private Long odontologoId;
    private String odontologoNombre;
    private String odontologoApellido;
    private String odontologoMatricula;
    private Long recepcionistaId;
    private String recepcionistaNombre;
    private String recepcionistaApellido;
    private LocalDateTime fechaTurno;
    private String estado;
    private String observaciones;

}
