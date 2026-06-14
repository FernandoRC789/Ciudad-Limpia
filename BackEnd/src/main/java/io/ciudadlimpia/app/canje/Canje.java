package io.ciudadlimpia.app.canje;

import io.ciudadlimpia.app.cupon.Cupon;
import io.ciudadlimpia.app.gaming_profile.GamingProfile;
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
public class Canje {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String codigoCanje;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gaming_profile_id", nullable = false)
    private GamingProfile gamingProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cupon_id", nullable = false)
    private Cupon cupon;

}
