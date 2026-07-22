package com.clinica.odontologica.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "odontologos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "nombre", "apellido", "matricula", "telefono", "email", "especialidad" })
@Builder
public class Odontologo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Column(nullable = false)
    private String matricula;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String especialidad;
    @OneToMany(mappedBy = "odontologo", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Turno> turnos = new HashSet<>();
}
