package io.ciudadlimpia.app.nivel;

import io.ciudadlimpia.app.gaming_profile.GamingProfile;
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
public class Nivel {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Integer puntosMinimos;

    @Column(length = 100)
    private String icono;

    @OneToMany(mappedBy = "nivel")
    private Set<GamingProfile> nivelGamingProfiles = new HashSet<>();

}
