package br.edu.fatecpg.backend_pulseira.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import br.edu.fatecpg.backend_pulseira.security.AcessoNegadoException;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Avisa os sites e apps abertos que algo mudou (telemetria nova, alerta novo ou atendido), por Server-Sent Events.
 * O evento só diz "o que" e "de quem" ({@code pessoaMonitoradaId}); quem recebe recarrega os dados pela API normal.
 * Cada conta só recebe eventos das pessoas que ela pode ver.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventosService {

    private record Assinante(ContaAutenticada conta, SseEmitter emitter) {}

    private final AcessoService acessoService;
    private final List<Assinante> assinantes = new CopyOnWriteArrayList<>();
    private final ExecutorService envio = Executors.newSingleThreadExecutor(r -> daemon(r, "eventos-envio"));
    private final ScheduledExecutorService agendador = Executors.newSingleThreadScheduledExecutor(r -> daemon(r, "eventos-batimento"));

    {
        // Linha de comentário a cada 20 s: mantém a conexão viva e derruba as mortas.
        agendador.scheduleAtFixedRate(this::batimento, 20, 20, TimeUnit.SECONDS);
    }

    public SseEmitter inscrever(ContaAutenticada conta) {
        SseEmitter emitter = new SseEmitter(0L);
        Assinante assinante = new Assinante(conta, emitter);
        assinantes.add(assinante);
        emitter.onCompletion(() -> assinantes.remove(assinante));
        emitter.onTimeout(() -> assinantes.remove(assinante));
        emitter.onError(falha -> assinantes.remove(assinante));
        try {
            emitter.send(SseEmitter.event().comment("conectado"));
        } catch (IOException falha) {
            assinantes.remove(assinante);
        }
        return emitter;
    }

    /** Publica sem bloquear quem chamou (a requisição da pulseira, por exemplo). */
    public void publicar(String pessoaMonitoradaId, String tipo) {
        if (pessoaMonitoradaId == null || assinantes.isEmpty()) {
            return;
        }
        envio.execute(() -> {
            String json = "{\"tipo\":\"" + tipo + "\",\"pessoaMonitoradaId\":\"" + pessoaMonitoradaId + "\"}";
            for (Assinante assinante : assinantes) {
                if (!podeVer(assinante.conta(), pessoaMonitoradaId)) {
                    continue;
                }
                try {
                    assinante.emitter().send(SseEmitter.event().name("mudanca").data(json));
                } catch (Exception falha) {
                    assinantes.remove(assinante);
                }
            }
        });
    }

    private boolean podeVer(ContaAutenticada conta, String pessoaMonitoradaId) {
        try {
            acessoService.exigirLeitura(conta, pessoaMonitoradaId);
            return true;
        } catch (AcessoNegadoException negado) {
            return false;
        } catch (Exception falha) {
            log.debug("Eventos: falha ao checar acesso: {}", falha.getMessage());
            return false;
        }
    }

    private void batimento() {
        for (Assinante assinante : assinantes) {
            try {
                assinante.emitter().send(SseEmitter.event().comment("ping"));
            } catch (Exception falha) {
                assinantes.remove(assinante);
            }
        }
    }

    private static Thread daemon(Runnable r, String nome) {
        Thread thread = new Thread(r, nome);
        thread.setDaemon(true);
        return thread;
    }

    @PreDestroy
    void encerrar() {
        envio.shutdownNow();
        agendador.shutdownNow();
    }
}
