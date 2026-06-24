package io.ciudadlimpia.app.nivel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface NivelRepository extends JpaRepository<Nivel, Integer> {
    Optional<Nivel>
    findFirstByPuntosMinimosGreaterThanOrderByPuntosMinimosAsc(
            Integer puntosMinimos
    );
}
