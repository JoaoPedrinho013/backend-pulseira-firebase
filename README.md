# Backend Pulseira — Firebase / Firestore

Cópia de teste do `backend-pulseira`, mantendo os endpoints e regras de domínio para continuar compatível com o site e o app existentes. Este serviço roda por padrão em `8080`, como alternativa ao backend Mongo atual (`8080`), e não compartilha os dados do Mongo.

## Iniciar no Windows

O projeto `zelo-pulseira-kotlin` já está configurado. Desligue o backend Mongo se estiver rodando e execute na pasta do clone:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-firebase.ps1
```

O script usa o JSON mais recente do projeto em Downloads. Se mover o arquivo, passe `-Credencial "C:\caminho\arquivo.json"`. Mantenha o terminal aberto durante a apresentação e pressione Ctrl+C para parar.

Em um clone novo, use Java 21 e execute `.\mvnw.cmd -DskipTests package` antes da primeira abertura. Baixe o JSON da conta de serviço na sua conta Firebase e configure a Web API Key no PowerShell com `$env:FIREBASE_WEB_API_KEY = 'sua-chave'` antes de iniciar.

No app, selecione o servidor `http://IP_DO_PC:8080`. Cadastre e pareie a pulseira no Firebase. O cadastro feito no Mongo pertence ao outro banco.

Para gravar o firmware a partir de um clone novo, copie `firmware/pulseira_iot/config.h.example` para `firmware/pulseira_iot/config.h` e preencha o Wi-Fi e o endereço da API.

## Usar outro projeto Firebase

1. Crie um projeto Firebase e habilite o Cloud Firestore em modo nativo.
2. Em Authentication, habilite o provedor **E-mail/senha**.
3. Gere uma chave de conta de serviço em **Configurações do projeto → Contas de serviço** e guarde o JSON fora desta pasta e fora do Git.
4. Copie a Web API Key em **Configurações do projeto → Geral**. Ela é usada no servidor para validar o login compatível com HTTP Basic.
5. Configure variáveis de ambiente antes de iniciar o backend:

```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS = 'C:\caminho\seguro\firebase-service-account.json'
$env:FIREBASE_PROJECT_ID = 'id-do-projeto'
$env:FIREBASE_WEB_API_KEY = 'web-api-key'
```

Opcionalmente, `ZELO_ADMIN_EMAIL` e `ZELO_ADMIN_SENHA` criam o administrador inicial. `ZELO_CORS_ORIGINS` define as origens do site. Mantenha o JSON da conta de serviço fora do Git.

## Compilar e executar manualmente

Use `mvnw.cmd spring-boot:run` no Windows (ou `./mvnw spring-boot:run` em Linux/macOS). Para rodar em outra porta, configure `SERVER_PORT`. O login atual dos clientes permanece e-mail/senha por HTTP Basic, mas a validação é feita no Firebase Authentication; o perfil da conta e os registros de domínio ficam no Firestore.

## Dados no Firestore

As coleções usadas são `cuidadores`, `pessoas_monitoradas`, `pulseiras`, `telemetrias`, `alertas` e `vinculos_cuidadores`. IDs de contas são os UIDs do Firebase Auth. Contas/pessoas e relacionamentos continuam seguindo os serviços de domínio copiados.

Esta cópia é para protótipo: os adaptadores fazem consultas de coleção no servidor e aplicam parte dos filtros em memória, adequado para dados pequenos de teste. Para volume maior, migre os filtros para consultas Firestore com índices compostos; configure regras e endureça autenticação dos endpoints de provisionamento/IoT antes de publicar.
Ligue apenas um backend por vez. A API usa TCP 8080 e a descoberta usa UDP 41234. Cadastre e pareie a pulseira uma vez no projeto Firebase.
