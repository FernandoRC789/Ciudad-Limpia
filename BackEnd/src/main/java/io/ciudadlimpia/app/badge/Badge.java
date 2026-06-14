package io.ciudadlimpia.app.badge;

import io.ciudadlimpia.app.gaming_profile_badge.GamingProfileBadge;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Badge {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(length = 100)
    private String icono;

    @Column(nullable = false)
    private String condicionTipo;

    @Column(nullable = false)
    private Integer condicionValor;

    @OneToMany(mappedBy = "badge")
    private Set<GamingProfileBadge> badgeGamingProfileBadges = new HashSet<>();

}
