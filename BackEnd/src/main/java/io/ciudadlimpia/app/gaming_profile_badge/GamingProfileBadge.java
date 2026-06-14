package io.ciudadlimpia.app.gaming_profile_badge;

import io.ciudadlimpia.app.badge.Badge;
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
public class GamingProfileBadge {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime obtenidoAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gaming_profile_id", nullable = false)
    private GamingProfile gamingProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

}
