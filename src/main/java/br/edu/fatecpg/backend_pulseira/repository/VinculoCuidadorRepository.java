package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;
import java.util.Optional;



import br.edu.fatecpg.backend_pulseira.model.VinculoCuidador;

public interface VinculoCuidadorRepository extends FirestoreRepository<VinculoCuidador> {
    List<VinculoCuidador> findByCuidadorIdAndAtivoTrue(String cuidadorId);
    boolean existsByCuidadorIdAndPessoaMonitoradaIdAndAtivoTrue(String cuidadorId, String pessoaMonitoradaId);
    Optional<VinculoCuidador> findByCuidadorIdAndPessoaMonitoradaId(String cuidadorId, String pessoaMonitoradaId);
    List<VinculoCuidador> findByPessoaMonitoradaIdAndAtivoTrue(String pessoaMonitoradaId);
    void deleteByCuidadorId(String cuidadorId);
}
