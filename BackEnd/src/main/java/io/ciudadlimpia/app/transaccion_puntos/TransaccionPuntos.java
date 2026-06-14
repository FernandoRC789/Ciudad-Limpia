package io.ciudadlimpia.app.transaccion_puntos;

import io.ciudadlimpia.app.gaming_profile.GamingProfile;
import io.ciudadlimpia.app.reporte.Reporte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class TransaccionPuntos {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer puntos;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, length = 150)
    private String motivo;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaming_profile_id", nullable = false)
    private GamingProfile gamingProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id")
    private Reporte reporte;

}
