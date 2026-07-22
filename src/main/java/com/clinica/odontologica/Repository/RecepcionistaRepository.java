package com.clinica.odontologica.Repository;

import com.clinica.odontologica.Entity.Recepcionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecepcionistaRepository extends JpaRepository<Recepcionista, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsuario(String usuario);

    Optional<Recepcionista> findByEmail(String email);

    Optional<Recepcionista> findByUsuario(String usuario);
}