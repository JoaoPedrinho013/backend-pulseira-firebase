package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Autoriza um cuidador a consultar os dados de uma pessoa monitorada.
 */
@Getter
@Setter
@NoArgsConstructor
public class VinculoCuidador {
    private String id;

    private String cuidadorId;
    private String pessoaMonitoradaId;
    private NivelAcesso nivelAcesso;
    private Boolean ativo;
    private Instant criadoEm;
}
