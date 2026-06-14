package io.ciudadlimpia.app.foto_reporte;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FotoReporteDTO {

    private Long id;

    @NotNull
    @Size(max = 500)
    private String urlFoto;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private Long reporte;

}
