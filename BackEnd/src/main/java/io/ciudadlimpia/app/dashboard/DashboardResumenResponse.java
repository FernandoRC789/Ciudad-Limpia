package io.ciudadlimpia.app.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResumenResponse {
    private long totalReportes;
    private long pendientes;
    private long enRevision;
    private long atendidos;
    private long rechazados;
    private long reportesHoy;
}