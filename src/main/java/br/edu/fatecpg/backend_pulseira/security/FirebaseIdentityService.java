package br.edu.fatecpg.backend_pulseira.security;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.Map;

/** Firebase Authentication account lifecycle plus email/password verification for the legacy API contract. */
@Service
public class FirebaseIdentityService {
    private final FirebaseAuth auth;
    private final RestClient http;
    private final String apiKey;

    public FirebaseIdentityService(FirebaseAuth auth,
            @Value("${firebase.web-api-key:}") String apiKey) {
        this.auth = auth;
        this.http = RestClient.create();
        this.apiKey = apiKey;
    }

    public UserRecord create(String email, String password, String displayName) {
        try { return auth.createUser(new UserRecord.CreateRequest().setEmail(email).setPassword(password).setDisplayName(displayName)); }
        catch (FirebaseAuthException e) { throw new IllegalArgumentException("Não foi possível criar a conta no Firebase Auth.", e); }
    }

    public String verify(String email, String password) {
        if (apiKey.isBlank()) throw new IllegalStateException("Configure FIREBASE_WEB_API_KEY para validar login e senha.");
        try {
            var response = http.post().uri("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={key}", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("email", email, "password", password, "returnSecureToken", true))
                    .retrieve().body(SignInResponse.class);
            if (response == null || response.localId() == null) throw new IllegalStateException("Resposta de login incompleta.");
            return response.localId();
        } catch (RestClientResponseException e) {
            throw new org.springframework.security.authentication.BadCredentialsException("E-mail ou senha incorretos.");
        }
    }

    public void updateEmail(String uid, String email) {
        try { auth.updateUser(new UserRecord.UpdateRequest(uid).setEmail(email)); }
        catch (FirebaseAuthException e) { throw new IllegalArgumentException("Não foi possível atualizar o e-mail no Firebase Auth.", e); }
    }
    public void updatePassword(String uid, String password) {
        try { auth.updateUser(new UserRecord.UpdateRequest(uid).setPassword(password)); }
        catch (FirebaseAuthException e) { throw new IllegalArgumentException("Não foi possível atualizar a senha no Firebase Auth.", e); }
    }
    public void setDisabled(String uid, boolean disabled) {
        try { auth.updateUser(new UserRecord.UpdateRequest(uid).setDisabled(disabled)); }
        catch (FirebaseAuthException e) { throw new IllegalArgumentException("Não foi possível alterar a conta no Firebase Auth.", e); }
    }
    public void delete(String uid) {
        try { auth.deleteUser(uid); } catch (FirebaseAuthException e) { throw new IllegalArgumentException("Não foi possível excluir a conta no Firebase Auth.", e); }
    }

    private record SignInResponse(@JsonProperty("localId") String localId) { }
}
