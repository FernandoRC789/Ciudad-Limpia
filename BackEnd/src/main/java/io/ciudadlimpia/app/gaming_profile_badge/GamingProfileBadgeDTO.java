package io.ciudadlimpia.app.gaming_profile_badge;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GamingProfileBadgeDTO {

    private Long id;

    @NotNull
    private OffsetDateTime obtenidoAt;

    @NotNull
    private Long gamingProfile;

    @NotNull
    private Integer badge;

}
