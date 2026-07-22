package com.clinica.odontologica.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recepcionistas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recepcionista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private String usuario;
    @OneToMany(mappedBy = "recepcionista", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Turno> turnos = new ArrayList<>();
}