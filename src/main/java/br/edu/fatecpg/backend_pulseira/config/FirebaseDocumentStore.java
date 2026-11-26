package br.edu.fatecpg.backend_pulseira.config;

import tools.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/** Small synchronous adapter for the existing domain services. */
public class FirebaseDocumentStore {
    private final Firestore firestore;
    private final ObjectMapper mapper;

    public FirebaseDocumentStore(Firestore firestore, ObjectMapper mapper) {
        this.firestore = firestore;
        this.mapper = mapper;
    }

    public <T> T save(String collection, T entity, Class<T> type) {
        try {
            String id = (String) mapper.convertValue(entity, Map.class).get("id");
            if (id == null || id.isBlank()) id = UUID.randomUUID().toString();
            Map<String, Object> values = mapper.convertValue(entity, Map.class);
            values.put("id", id);
            firestore.collection(collection).document(id).set(values).get();
            values.put("id", id);
            return mapper.convertValue(values, type);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Gravação no Firestore interrompida.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Não foi possível gravar no Firestore.", e.getCause());
        }
    }

    public <T> Optional<T> findById(String collection, String id, Class<T> type) {
        try {
            DocumentSnapshot doc = firestore.collection(collection).document(id).get().get();
            return doc.exists() ? Optional.of(mapper.convertValue(doc.getData(), type)) : Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Leitura do Firestore interrompida.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Não foi possível ler o Firestore.", e.getCause());
        }
    }

    public <T> List<T> findAll(String collection, Class<T> type) {
        try {
            return firestore.collection(collection).get().get().getDocuments().stream()
                    .map(doc -> mapper.convertValue(doc.getData(), type)).toList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Leitura do Firestore interrompida.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Não foi possível listar o Firestore.", e.getCause());
        }
    }

    public void deleteById(String collection, String id) {
        try { firestore.collection(collection).document(id).delete().get(); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IllegalStateException(e); }
        catch (ExecutionException e) { throw new IllegalStateException("Não foi possível excluir do Firestore.", e.getCause()); }
    }
}
