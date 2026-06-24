package io.ciudadlimpia.app.gaming_profile;

import io.ciudadlimpia.app.canje.Canje;
import io.ciudadlimpia.app.gaming_profile_badge.GamingProfileBadge;
import io.ciudadlimpia.app.nivel.Nivel;
import io.ciudadlimpia.app.transaccion_puntos.TransaccionPuntos;
import io.ciudadlimpia.app.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GamingProfile {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer puntosTotales;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nivel_id", nullable = false)
    private Nivel nivel;

    @OneToMany(mappedBy = "gamingProfile")
    private Set<GamingProfileBadge> gamingProfileGamingProfileBadges = new HashSet<>();

    @OneToMany(mappedBy = "gamingProfile")
    private Set<Canje> gamingProfileCanjes = new HashSet<>();

    @OneToMany(mappedBy = "gamingProfile")
    private Set<TransaccionPuntos> gamingProfileTransaccionPuntoses = new HashSet<>();
}
