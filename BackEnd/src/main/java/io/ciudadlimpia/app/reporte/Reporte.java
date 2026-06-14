package io.ciudadlimpia.app.reporte;

import io.ciudadlimpia.app.foto_reporte.FotoReporte;
import io.ciudadlimpia.app.historial_estado.HistorialEstado;
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
import java.math.BigDecimal;
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
public class Reporte {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "longtext")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column
    private String direccion;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(nullable = false)
    private Integer puntosOtorgados = 0;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "reporte")
    private Set<FotoReporte> reporteFotoReportes = new HashSet<>();

    @OneToMany(mappedBy = "reporte")
    private Set<HistorialEstado> reporteHistorialEstadoes = new HashSet<>();

    @OneToMany(mappedBy = "reporte")
    private Set<TransaccionPuntos> reporteTransaccionPuntoses = new HashSet<>();

}
