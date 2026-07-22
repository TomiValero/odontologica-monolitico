package com.clinica.odontologica.Repository;

import com.clinica.odontologica.Entity.Odontologo;
import com.clinica.odontologica.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {
    Optional<Odontologo> findByMatricula(String matricula);

    boolean existsByMatricula(String matricula);

    boolean existsByEmail(String email);

}
