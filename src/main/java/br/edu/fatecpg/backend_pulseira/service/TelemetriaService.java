package br.edu.fatecpg.backend_pulseira.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.model.StatusAlerta;
import br.edu.fatecpg.backend_pulseira.model.StatusPulseira;
import br.edu.fatecpg.backend_pulseira.model.Telemetria;
import br.edu.fatecpg.backend_pulseira.model.TipoAlerta;
import br.edu.fatecpg.backend_pulseira.repository.AlertaRepository;
import br.edu.fatecpg.backend_pulseira.repository.TelemetriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelemetriaService {

    private static final Duration JANELA_REPETICAO = Duration.ofSeconds(30);

    private final TelemetriaRepository telemetriaRepository;
    private final AlertaRepository alertaRepository;
    private final PulseiraService pulseiraService;
    private final PessoaMonitoradaService pessoaService;
    private final EventosService eventosService;

    public Telemetria registrar(Telemetria telemetria) {
        if (telemetria.getDispositivoId() == null || telemetria.getDispositivoId().isBlank()) {
            throw new IllegalArgumentException("O identificador do dispositivo é obrigatório.");
        }
        validarCoordenada(telemetria.getLatitude(), -90, 90, "latitude");
        validarCoordenada(telemetria.getLongitude(), -180, 180, "longitude");

        Pulseira pulseira = pulseiraService.buscarPorDispositivo(telemetria.getDispositivoId().trim());
        if (pulseira.getStatus() != StatusPulseira.ATIVA || pulseira.getPessoaMonitoradaId() == null) {
            throw new IllegalStateException("A pulseira precisa estar ativa e vinculada para enviar telemetria.");
        }
        pessoaService.buscarAtiva(pulseira.getPessoaMonitoradaId());
        pulseiraService.registrarSinal(pulseira);

        // Os vínculos vêm do cadastro da pulseira, nunca do dispositivo externo.
        telemetria.setDispositivoId(pulseira.getDispositivoId());
        telemetria.setPulseiraId(pulseira.getId());
        telemetria.setPessoaMonitoradaId(pulseira.getPessoaMonitoradaId());
        telemetria.setRegistradoEm(telemetria.getRegistradoEm() == null ? Instant.now() : telemetria.getRegistradoEm());

        Telemetria salva = telemetriaRepository.save(telemetria);
        if (Boolean.TRUE.equals(salva.getQuedaDetectada())) {
            gerarAlerta(salva, TipoAlerta.POSSIVEL_QUEDA);
        }
        if (Boolean.TRUE.equals(salva.getPanicoAcionado())) {
            gerarAlerta(salva, TipoAlerta.PANICO);
        }
        eventosService.publicar(salva.getPessoaMonitoradaId(), "telemetria");
        return salva;
    }

    private void gerarAlerta(Telemetria telemetria, TipoAlerta tipo) {
        // A pulseira repete o envio até receber 2xx; se a resposta demorar, o mesmo evento chega mais de uma vez.
        // Não cria outro alerta enquanto houver um aberto do mesmo tipo, nem logo após o último.
        String pulseiraId = telemetria.getPulseiraId();
        if (alertaRepository.existsByPulseiraIdAndTipoAndStatusIn(
                        pulseiraId, tipo, List.of(StatusAlerta.PENDENTE, StatusAlerta.RECONHECIDO))
                || alertaRepository.existsByPulseiraIdAndTipoAndGeradoEmGreaterThanEqual(
                        pulseiraId, tipo, Instant.now().minus(JANELA_REPETICAO))) {
            return;
        }
        Alerta alerta = new Alerta();
        alerta.setTipo(tipo);
        alerta.setStatus(StatusAlerta.PENDENTE);
        alerta.setPessoaMonitoradaId(telemetria.getPessoaMonitoradaId());
        alerta.setPulseiraId(telemetria.getPulseiraId());
        alerta.setTelemetriaId(telemetria.getId());
        alerta.setGeradoEm(Instant.now());
        alertaRepository.save(alerta);
        eventosService.publicar(telemetria.getPessoaMonitoradaId(), "alerta");
    }

    private void validarCoordenada(Double valor, double minimo, double maximo, String nome) {
        if (valor != null && (valor < minimo || valor > maximo)) {
            throw new IllegalArgumentException("Valor de " + nome + " inválido.");
        }
    }
}
