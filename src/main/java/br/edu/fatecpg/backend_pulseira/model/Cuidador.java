package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;
import java.time.LocalDate;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Conta que acessa a aplicação para acompanhar pessoas monitoradas.
 * A senha nunca é armazenada; somente o hash gerado pela camada de segurança.
 */
@Getter
@Setter
@NoArgsConstructor
public class Cuidador {
    private String id;

    private String nome;
    private String email;
    private String telefone;

    /** Somente dígitos. Índice sparse: contas antigas, sem CPF, não colidem. */
    private String cpf;

    private LocalDate dataNascimento;

    /** Ausente em contas antigas; nesse caso a conta é tratada como RESPONSAVEL. */
    private PerfilConta perfil;

    /** Preenchido apenas em contas PACIENTE: a pessoa monitorada que a conta representa. */
    private String pessoaMonitoradaId;

    /** Foto de perfil como data URL (JPEG/PNG/WebP em Base64), já reduzida pelo site. */
    private String fotoUrl;

    private Endereco endereco;
    private Boolean ativo;
    private Instant criadoEm;
}
