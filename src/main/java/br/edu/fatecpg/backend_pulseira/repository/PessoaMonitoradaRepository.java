package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;
import java.util.Optional;



import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;

public interface PessoaMonitoradaRepository extends FirestoreRepository<PessoaMonitorada> {
    List<PessoaMonitorada> findByAtivaTrue();
    Optional<PessoaMonitorada> findByCodigoVinculo(String codigoVinculo);
    boolean existsByCodigoVinculo(String codigoVinculo);
}
