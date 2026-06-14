package io.ciudadlimpia.app.cupon;

import io.ciudadlimpia.app.canje.Canje;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Cupon {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Integer costoPuntos;

    @Column(nullable = false)
    private Integer stock;

    @Column
    private OffsetDateTime fechaExpiracion;

    @Column(nullable = false, columnDefinition = "tinyint", length = 1)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "cupon")
    private Set<Canje> cuponCanjes = new HashSet<>();

}
