package io.ciudadlimpia.app.dashboard;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class ZonaCalorResponse {
    private Double lat;
    private Double lng;
    private Long totalReportes;

    // Constructor explícito que Hibernate puede usar en JPQL
    public ZonaCalorResponse(BigDecimal lat, BigDecimal lng, Long totalReportes) {
        this.lat = lat != null ? lat.doubleValue() : null;
        this.lng = lng != null ? lng.doubleValue() : null;
        this.totalReportes = totalReportes;
    }
}