package com.clinica.odontologica.Repository;

import com.clinica.odontologica.Entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Optional<Turno> findByOdontologoIdAndFechaTurno(Long odontologoId, LocalDateTime fechaTurno);

    Optional<Turno> findBypacienteIdAndFechaTurno(Long pacienteId, LocalDateTime fechaTurno);

    List<Turno> findByOdontologoId(Long odontologoId);

    List<Turno> findByPacienteId(Long pacienteId);

    List<Turno> findByRecepcionistaId(Long repcionistaId);
}
