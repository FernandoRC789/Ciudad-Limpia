package io.ciudadlimpia.app.canje;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CanjeDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String codigoCanje;

    @NotNull
    @Size(max = 255)
    private String estado;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private Long gamingProfile;

    @NotNull
    private Integer cupon;

}
