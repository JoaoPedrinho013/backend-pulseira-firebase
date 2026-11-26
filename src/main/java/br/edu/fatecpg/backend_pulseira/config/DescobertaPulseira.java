package br.edu.fatecpg.backend_pulseira.config;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Deixa a pulseira achar o servidor sozinha, sem IP fixo no firmware: ela manda um broadcast UDP "ZELO?"
 * para a rede e este serviço responde "ZELO &lt;porta&gt;". O endereço de quem respondeu é o do servidor.
 * Só funciona na mesma rede local (e se o Wi-Fi não isolar os aparelhos entre si).
 */
@Slf4j
@Component
public class DescobertaPulseira {

    private final int portaUdp;
    private final int portaHttp;
    private DatagramSocket socket;

    public DescobertaPulseira(
            @Value("${zelo.descoberta.porta:41234}") int portaUdp,
            @Value("${server.port:8080}") int portaHttp) {
        this.portaUdp = portaUdp;
        this.portaHttp = portaHttp;
    }

    @PostConstruct
    void iniciar() {
        try {
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(portaUdp));
        } catch (Exception falha) {
            log.warn("Descoberta da pulseira desligada (UDP {} indisponível): {}", portaUdp, falha.getMessage());
            return;
        }
        Thread escuta = new Thread(this::escutar, "descoberta-pulseira");
        escuta.setDaemon(true);
        escuta.start();
        log.info("Descoberta da pulseira ouvindo em UDP {}", portaUdp);
    }

    private void escutar() {
        byte[] buffer = new byte[64];
        while (!socket.isClosed()) {
            try {
                DatagramPacket pedido = new DatagramPacket(buffer, buffer.length);
                socket.receive(pedido);
                String texto = new String(pedido.getData(), 0, pedido.getLength(), StandardCharsets.UTF_8).trim();
                if (!texto.equals("ZELO?")) {
                    continue;
                }
                byte[] resposta = ("ZELO " + portaHttp).getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(resposta, resposta.length, pedido.getAddress(), pedido.getPort()));
            } catch (Exception falha) {
                if (!socket.isClosed()) {
                    log.debug("Descoberta da pulseira: {}", falha.getMessage());
                }
            }
        }
    }

    @PreDestroy
    void encerrar() {
        if (socket != null) {
            socket.close();
        }
    }
}
