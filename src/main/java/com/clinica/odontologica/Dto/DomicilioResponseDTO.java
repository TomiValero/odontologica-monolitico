package com.clinica.odontologica.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomicilioResponseDTO {

    private Long id;
    private String calle;
    private String localidad;
    private String provincia;
}