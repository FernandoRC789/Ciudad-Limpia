package io.ciudadlimpia.app.usuario;

import io.ciudadlimpia.app.gaming_profile.GamingProfile;
import io.ciudadlimpia.app.historial_estado.HistorialEstado;
import io.ciudadlimpia.app.reporte.Reporte;
import io.ciudadlimpia.app.rol.Rol;
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
public class Usuario {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String dni;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(length = 20)
    private String telefono;

    @Column
    private String fotoPerfil;

    @Column(nullable = false, columnDefinition = "tinyint", length = 1)
    private Boolean activo;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String direccion;

    @Column
    private String distrito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario")
    private Set<GamingProfile> usuarioGamingProfiles = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<Reporte> usuarioReportes = new HashSet<>();

    @OneToMany(mappedBy = "admin")
    private Set<HistorialEstado> adminHistorialEstadoes = new HashSet<>();

}
