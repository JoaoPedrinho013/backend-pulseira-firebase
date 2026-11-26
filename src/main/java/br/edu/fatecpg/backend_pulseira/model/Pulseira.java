package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cadastro da pulseira física e seu vínculo atual com a pessoa monitorada.
 */
@Getter
@Setter
@NoArgsConstructor
public class Pulseira {
    private String id;
    private String dispositivoId;

    private String pessoaMonitoradaId;
    private StatusPulseira status;
    private Instant vinculadaEm;
    private Instant atualizadaEm;

    /** Última vez que a pulseira se comunicou com o servidor (leitura ou consulta de alarme). */
    private Instant ultimoSinalEm;
}
