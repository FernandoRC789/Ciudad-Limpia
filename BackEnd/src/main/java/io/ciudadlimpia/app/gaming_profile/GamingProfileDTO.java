package io.ciudadlimpia.app.gaming_profile;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GamingProfileDTO {

    private Long id;

    @NotNull
    private Integer puntosTotales;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @NotNull
    private Long usuario;

    @NotNull
    private Integer nivel;

}
