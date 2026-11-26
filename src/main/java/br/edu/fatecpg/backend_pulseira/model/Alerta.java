package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registro de um evento que exige atenção do cuidador. A telemetria mantém a
 * leitura bruta; este documento acompanha o atendimento do alerta.
 */
@Getter
@Setter
@NoArgsConstructor
public class Alerta {
    private String id;

    private TipoAlerta tipo;
    private StatusAlerta status;
    private String pessoaMonitoradaId;
    private String pulseiraId;
    private String telemetriaId;
    private Instant geradoEm;
    private Instant reconhecidoEm;
    private String cuidadorReconheceuId;
    private Instant resolvidoEm;
    private String observacao;
}
