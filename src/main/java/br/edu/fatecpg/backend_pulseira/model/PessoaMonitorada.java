package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;
import java.time.LocalDate;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pessoa que utiliza a pulseira. Ela não precisa, necessariamente, ter conta
 * de acesso ao aplicativo.
 */
@Getter
@Setter
@NoArgsConstructor
public class PessoaMonitorada {
    private String id;

    private String nome;
    private LocalDate dataNascimento;
    private String telefone;
    private Endereco endereco;
    private InformacoesMedicas informacoesMedicas;
    private Boolean ativa;
    private Instant criadoEm;

    /** Código que a pessoa compartilha para que um responsável se vincule a ela. */
    private String codigoVinculo;
}
