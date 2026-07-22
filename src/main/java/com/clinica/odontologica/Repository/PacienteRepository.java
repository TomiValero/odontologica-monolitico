package com.clinica.odontologica.Repository;

import com.clinica.odontologica.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCedula(String cedula);

    Optional<Paciente> findByCedula(String cedula);
}