package br.edu.fatecpg.backend_pulseira.repository;

import java.util.Optional;



import br.edu.fatecpg.backend_pulseira.model.Cuidador;

public interface CuidadorRepository extends FirestoreRepository<Cuidador> {
    Optional<Cuidador> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<Cuidador> findByPessoaMonitoradaId(String pessoaMonitoradaId);
}
