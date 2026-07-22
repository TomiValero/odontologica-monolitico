package com.clinica.odontologica.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "turnos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", referencedColumnName = "id", nullable = false)
    private Paciente paciente;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "odontologo_id", referencedColumnName = "id", nullable = false)
    private Odontologo odontologo;
    @Column(nullable = false)
    private LocalDateTime fechaTurno;
    @Column(nullable = false)
    private String estado;
    @Column
    private String observaciones;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recepcionista_id", referencedColumnName = "id", nullable = false)
    private Recepcionista recepcionista;

}
