package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;
import java.util.Optional;



import br.edu.fatecpg.backend_pulseira.model.Pulseira;

public interface PulseiraRepository extends FirestoreRepository<Pulseira> {
    Optional<Pulseira> findByDispositivoId(String dispositivoId);
    List<Pulseira> findByPessoaMonitoradaId(String pessoaMonitoradaId);
}
