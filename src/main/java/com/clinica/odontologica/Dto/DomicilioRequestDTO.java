package com.clinica.odontologica.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomicilioRequestDTO {

    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    @NotBlank(message = "La localidad es obligatoria")
    private String localidad;

    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;
}