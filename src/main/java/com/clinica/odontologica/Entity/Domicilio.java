package com.clinica.odontologica.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "domicilios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Domicilio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String calle;
    @Column
    private String localidad;
    @Column
    private String provincia;
}
