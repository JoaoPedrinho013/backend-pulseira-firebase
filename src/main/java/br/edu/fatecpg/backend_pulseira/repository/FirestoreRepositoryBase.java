package br.edu.fatecpg.backend_pulseira.repository;

import br.edu.fatecpg.backend_pulseira.config.FirebaseDocumentStore;
import java.util.List;
import java.util.Optional;

abstract class FirestoreRepositoryBase<T> implements FirestoreRepository<T> {
    private final FirebaseDocumentStore store;
    private final String collection;
    private final Class<T> type;
    FirestoreRepositoryBase(FirebaseDocumentStore store, String collection, Class<T> type) {
        this.store = store; this.collection = collection; this.type = type;
    }
    public T save(T value) { return store.save(collection, value, type); }
    public Optional<T> findById(String id) { return store.findById(collection, id, type); }
    public List<T> findAll() { return store.findAll(collection, type); }
    public void deleteById(String id) { store.deleteById(collection, id); }
}
