package br.edu.fatecpg.backend_pulseira.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;
import br.edu.fatecpg.backend_pulseira.service.EventosService;

import lombok.RequiredArgsConstructor;

/** Canal de eventos em tempo real (SSE) para o site: avisa quando algo muda, sem precisar ficar consultando. */
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventosController {

    private final EventosService eventosService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter assinar(@AuthenticationPrincipal ContaAutenticada conta) {
        return eventosService.inscrever(conta);
    }
}
