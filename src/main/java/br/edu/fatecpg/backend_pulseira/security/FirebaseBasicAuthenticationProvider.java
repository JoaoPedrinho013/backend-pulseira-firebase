package br.edu.fatecpg.backend_pulseira.security;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.repository.CuidadorRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Locale;

@Component
public class FirebaseBasicAuthenticationProvider implements AuthenticationProvider {
    private final FirebaseIdentityService identity;
    private final CuidadorRepository contas;
    public FirebaseBasicAuthenticationProvider(FirebaseIdentityService identity, CuidadorRepository contas) { this.identity = identity; this.contas = contas; }
    @Override public Authentication authenticate(Authentication request) throws AuthenticationException {
        String email = request.getName().trim().toLowerCase(Locale.ROOT);
        String uid = identity.verify(email, String.valueOf(request.getCredentials()));
        Cuidador conta = contas.findById(uid).orElseThrow(() -> new UsernameNotFoundException("Conta não encontrada."));
        if (!Boolean.TRUE.equals(conta.getAtivo())) throw new BadCredentialsException("Conta desativada.");
        ContaAutenticada principal = new ContaAutenticada(conta);
        return UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities());
    }
    @Override public boolean supports(Class<?> type) { return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type); }
}
