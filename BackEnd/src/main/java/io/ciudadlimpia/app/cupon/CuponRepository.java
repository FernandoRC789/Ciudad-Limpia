package io.ciudadlimpia.app.cupon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CuponRepository extends JpaRepository<Cupon, Integer> {

    // Solo cupones activos con stock disponible
    List<Cupon> findByActivoTrueAndStockGreaterThan(int stock);
}
