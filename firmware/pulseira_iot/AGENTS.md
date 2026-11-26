# Orientação para configurar a pulseira IoT

Use este arquivo como guia rápido em futuras sessões sobre a pulseira. O escopo é somente `firmware/pulseira_iot/` e a gravação da NodeMCU pela Arduino IDE. Não altere o app mobile, o site ou o código do back-end ao configurar uma rede; explique antes se parecer necessário mudar algum deles.

## Arquivos importantes

- `pulseira_iot.ino`: firmware ESP8266/NodeMCU ESP-12E. Só editar se o usuário pedir mudança de comportamento.
- `config.h`: dados locais de Wi-Fi e identificação/endereço do servidor. Para trocar de rede, normalmente basta atualizar aqui e gravar novamente.
- `README.md`: instruções completas, placa, portas, ligações e diagnóstico.

Não leia em voz alta, copie para respostas, registre em logs ou inclua em commits os valores atuais de `WIFI_PASSWORD*`. Trate SSIDs, senhas e URLs privadas como dados do usuário. Antes de compartilhar alterações, confira que `config.h` não está incluindo credenciais reais no diff. Nunca invente nem reutilize uma senha passada anteriormente: peça/receba os dados da rede atual.

## Roteiro para uma nova rede / hotspot do celular

1. Peça ao usuário o nome (SSID) e a senha atuais. A placa ESP-12E só entra em Wi-Fi de 2,4 GHz. Se usar hotspot do celular, ativar compatibilidade/2,4 GHz quando houver essa opção; deixar o hotspot ativo durante conexão e teste.
2. O computador que roda o back-end, o celular (se for usar o app) e a pulseira precisam estar na mesma rede local. No hotspot, conecte o computador ao hotspot do celular. Redes de faculdade/públicas com portal de login, isolamento de clientes ou bloqueio de comunicação entre aparelhos podem impedir a pulseira de alcançar o computador; prefira hotspot próprio ou rede privada.
3. Confirme que o back-end está rodando na porta 8080 e que o firewall do computador permite conexões locais TCP 8080 e UDP 41234. A descoberta automática implementada em `config/DescobertaPulseira.java` responde ao broadcast `ZELO?` na UDP 41234 com a porta HTTP do servidor. Ela funciona apenas na mesma LAN e quando o roteador/hotspot permite tráfego entre clientes.
4. Em `config.h`, atualize `WIFI_SSID`/`WIFI_PASSWORD`. Se necessário, use `WIFI_SSID_2`/`WIFI_PASSWORD_2` ou o par 3 para redes alternativas; SSID vazio desativa aquela opção. O firmware examina as redes configuradas e escolhe a de melhor sinal.
5. `TELEMETRY_URL` deve manter `/api/telemetrias`. Para a LAN, use `http://IP_DO_COMPUTADOR:8080/api/telemetrias`. A descoberta UDP tenta encontrar o IP automaticamente (o que ajuda quando o DHCP muda), mas o IP escrito na URL é a reserva; portanto atualize-o para o IPv4 atual do computador (`ipconfig`) quando a rede mudar. Evite `localhost` e `10.0.2.2`: são endereços que não apontam ao PC para a placa física. HTTPS/túnel só se o usuário precisar acessar de fora da LAN; URL pública pode mudar e precisa ser atualizada.
6. Preserve `DEVICE_ID` salvo se o usuário disser que está usando outra pulseira. Ele precisa corresponder exatamente ao `dispositivoId` cadastrado, ativo e vinculado no back-end. Não altere o cadastro ou dados do back-end sem pedido explícito.
7. Na Arduino IDE, abra `pulseira_iot.ino` (com `config.h` na mesma pasta), selecione a placa `NodeMCU 1.0 (ESP-12E Module)` e a porta COM da placa, então clique em Carregar. Se o computador não reconhecer a placa, verificar cabo de dados/driver CH340. Se falhar a gravação, não manter o botão FLASH/D3 pressionado durante a inicialização.
8. Abra o Monitor Serial em 115200 baud e reinicie a placa parada por 2 segundos para calibrar. Confirme `Wi-Fi conectado ...` e telemetria com HTTP 2xx (normalmente 201). Só então teste pelo app/site. O app no celular físico deve apontar para `http://IP_DO_COMPUTADOR:8080`; `10.0.2.2` é exclusivo do emulador.

## Diagnóstico rápido

- Sem Wi-Fi: conferir SSID/senha, 2,4 GHz, alcance e hotspot ativo.
- Wi-Fi conectado, envio falha (`-1`): conferir back-end ativo, IP/porta, firewall e se a rede deixa os dispositivos conversarem; conferir descoberta UDP 41234 ou usar IP atual na URL reserva.
- HTTP 400: conferir `DEVICE_ID`, cadastro, status ativo e vínculo da pulseira.
- HTTP 401/403: conferir segurança/autorização do back-end; não tentar corrigir alterando o firmware sem investigar a configuração atual.
- Ao relatar resultado, não revelar credenciais. Não editar frontends nem código do back-end como parte do procedimento.
