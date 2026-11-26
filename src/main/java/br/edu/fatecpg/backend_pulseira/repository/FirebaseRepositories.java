package br.edu.fatecpg.backend_pulseira.repository;

import br.edu.fatecpg.backend_pulseira.config.FirebaseDocumentStore;
import br.edu.fatecpg.backend_pulseira.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
class CuidadorFirestoreRepository extends FirestoreRepositoryBase<Cuidador> implements CuidadorRepository {
    CuidadorFirestoreRepository(FirebaseDocumentStore s) { super(s, "cuidadores", Cuidador.class); }
    public Optional<Cuidador> findByEmail(String v) { return findAll().stream().filter(x -> v.equalsIgnoreCase(x.getEmail())).findFirst(); }
    public boolean existsByEmail(String v) { return findByEmail(v).isPresent(); }
    public boolean existsByCpf(String v) { return findAll().stream().anyMatch(x -> v.equals(x.getCpf())); }
    public Optional<Cuidador> findByPessoaMonitoradaId(String v) { return findAll().stream().filter(x -> v.equals(x.getPessoaMonitoradaId())).findFirst(); }
}

@Repository
class PessoaMonitoradaFirestoreRepository extends FirestoreRepositoryBase<PessoaMonitorada> implements PessoaMonitoradaRepository {
    PessoaMonitoradaFirestoreRepository(FirebaseDocumentStore s) { super(s, "pessoas_monitoradas", PessoaMonitorada.class); }
    public List<PessoaMonitorada> findByAtivaTrue() { return findAll().stream().filter(x -> Boolean.TRUE.equals(x.getAtiva())).toList(); }
    public Optional<PessoaMonitorada> findByCodigoVinculo(String v) { return findAll().stream().filter(x -> v.equalsIgnoreCase(x.getCodigoVinculo())).findFirst(); }
    public boolean existsByCodigoVinculo(String v) { return findByCodigoVinculo(v).isPresent(); }
}

@Repository
class PulseiraFirestoreRepository extends FirestoreRepositoryBase<Pulseira> implements PulseiraRepository {
    PulseiraFirestoreRepository(FirebaseDocumentStore s) { super(s, "pulseiras", Pulseira.class); }
    public Optional<Pulseira> findByDispositivoId(String v) { return findAll().stream().filter(x -> v.equals(x.getDispositivoId())).findFirst(); }
    public List<Pulseira> findByPessoaMonitoradaId(String v) { return findAll().stream().filter(x -> v.equals(x.getPessoaMonitoradaId())).toList(); }
}

@Repository
class VinculoCuidadorFirestoreRepository extends FirestoreRepositoryBase<VinculoCuidador> implements VinculoCuidadorRepository {
    VinculoCuidadorFirestoreRepository(FirebaseDocumentStore s) { super(s, "vinculos_cuidadores", VinculoCuidador.class); }
    public List<VinculoCuidador> findByCuidadorIdAndAtivoTrue(String v) { return findAll().stream().filter(x -> v.equals(x.getCuidadorId()) && Boolean.TRUE.equals(x.getAtivo())).toList(); }
    public boolean existsByCuidadorIdAndPessoaMonitoradaIdAndAtivoTrue(String c, String p) { return findAll().stream().anyMatch(x -> c.equals(x.getCuidadorId()) && p.equals(x.getPessoaMonitoradaId()) && Boolean.TRUE.equals(x.getAtivo())); }
    public Optional<VinculoCuidador> findByCuidadorIdAndPessoaMonitoradaId(String c, String p) { return findAll().stream().filter(x -> c.equals(x.getCuidadorId()) && p.equals(x.getPessoaMonitoradaId())).findFirst(); }
    public List<VinculoCuidador> findByPessoaMonitoradaIdAndAtivoTrue(String v) { return findAll().stream().filter(x -> v.equals(x.getPessoaMonitoradaId()) && Boolean.TRUE.equals(x.getAtivo())).toList(); }
    public void deleteByCuidadorId(String v) { findAll().stream().filter(x -> v.equals(x.getCuidadorId())).forEach(x -> deleteById(x.getId())); }
}

@Repository
class TelemetriaFirestoreRepository extends FirestoreRepositoryBase<Telemetria> implements TelemetriaRepository {
    TelemetriaFirestoreRepository(FirebaseDocumentStore s) { super(s, "telemetrias", Telemetria.class); }
    private Comparator<Telemetria> recency() { return Comparator.comparing(Telemetria::getRegistradoEm, Comparator.nullsLast(Comparator.naturalOrder())).reversed(); }
    public Optional<Telemetria> findFirstByDispositivoIdOrderByRegistradoEmDesc(String v) { return findAll().stream().filter(x -> v.equals(x.getDispositivoId())).sorted(recency()).findFirst(); }
    public Optional<Telemetria> findFirstByPessoaMonitoradaIdOrderByRegistradoEmDesc(String v) { return findAll().stream().filter(x -> v.equals(x.getPessoaMonitoradaId())).sorted(recency()).findFirst(); }
    public List<Telemetria> findByDispositivoIdAndRegistradoEmBetweenOrderByRegistradoEmDesc(String v, Instant a, Instant b) { return findAll().stream().filter(x -> v.equals(x.getDispositivoId()) && between(x.getRegistradoEm(), a, b)).sorted(recency()).toList(); }
    public List<Telemetria> findByDispositivoIdAndQuedaDetectadaTrueOrderByRegistradoEmDesc(String v) { return findAll().stream().filter(x -> v.equals(x.getDispositivoId()) && Boolean.TRUE.equals(x.getQuedaDetectada())).sorted(recency()).toList(); }
    public List<Telemetria> findByPessoaMonitoradaIdAndRegistradoEmBetweenOrderByRegistradoEmDesc(String v, Instant a, Instant b) { return findAll().stream().filter(x -> v.equals(x.getPessoaMonitoradaId()) && between(x.getRegistradoEm(), a, b)).sorted(recency()).toList(); }
    public List<Telemetria> findByPessoaMonitoradaIdAndQuedaDetectadaTrueOrderByRegistradoEmDesc(String v) { return findAll().stream().filter(x -> v.equals(x.getPessoaMonitoradaId()) && Boolean.TRUE.equals(x.getQuedaDetectada())).sorted(recency()).toList(); }
    private boolean between(Instant t, Instant a, Instant b) { return t != null && !t.isBefore(a) && !t.isAfter(b); }
}

@Repository
class AlertaFirestoreRepository extends FirestoreRepositoryBase<Alerta> implements AlertaRepository {
    AlertaFirestoreRepository(FirebaseDocumentStore s) { super(s, "alertas", Alerta.class); }
    private Comparator<Alerta> recency() { return Comparator.comparing(Alerta::getGeradoEm, Comparator.nullsLast(Comparator.naturalOrder())).reversed(); }
    public List<Alerta> findByPessoaMonitoradaIdAndStatusOrderByGeradoEmDesc(String p, StatusAlerta st) { return findByPessoaMonitoradaIdOrderByGeradoEmDesc(p).stream().filter(x -> st == x.getStatus()).toList(); }
    public List<Alerta> findByPessoaMonitoradaIdOrderByGeradoEmDesc(String p) { return findAll().stream().filter(x -> p.equals(x.getPessoaMonitoradaId())).sorted(recency()).toList(); }
    public Optional<Alerta> findFirstByPessoaMonitoradaIdOrderByGeradoEmDesc(String p) { return findByPessoaMonitoradaIdOrderByGeradoEmDesc(p).stream().findFirst(); }
    public Page<Alerta> findByPessoaMonitoradaId(String p, Pageable page) { return pagina(findByPessoaMonitoradaIdOrderByGeradoEmDesc(p), page); }
    public Page<Alerta> findByPessoaMonitoradaIdAndGeradoEmGreaterThanEqual(String p, Instant desde, Pageable page) { return pagina(findByPessoaMonitoradaIdOrderByGeradoEmDesc(p).stream().filter(x -> x.getGeradoEm() != null && !x.getGeradoEm().isBefore(desde)).toList(), page); }
    public boolean existsByPulseiraIdAndTipoAndStatusIn(String p, TipoAlerta t, List<StatusAlerta> s) { return findAll().stream().anyMatch(x -> p.equals(x.getPulseiraId()) && t == x.getTipo() && s.contains(x.getStatus())); }
    public boolean existsByPulseiraIdAndTipoAndGeradoEmGreaterThanEqual(String p, TipoAlerta t, Instant since) { return findAll().stream().anyMatch(x -> p.equals(x.getPulseiraId()) && t == x.getTipo() && x.getGeradoEm() != null && !x.getGeradoEm().isBefore(since)); }
    public long countByPessoaMonitoradaIdAndStatus(String p, StatusAlerta s) { return findAll().stream().filter(x -> p.equals(x.getPessoaMonitoradaId()) && s == x.getStatus()).count(); }
    private Page<Alerta> pagina(List<Alerta> all, Pageable p) { int start = (int)Math.min(p.getOffset(), all.size()); int end = Math.min(start + p.getPageSize(), all.size()); return new PageImpl<>(all.subList(start, end), p, all.size()); }
}
