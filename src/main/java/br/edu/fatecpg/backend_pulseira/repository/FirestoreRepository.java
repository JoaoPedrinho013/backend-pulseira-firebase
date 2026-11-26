package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;
import java.util.Optional;

public interface FirestoreRepository<T> {
    T save(T value);
    Optional<T> findById(String id);
    List<T> findAll();
    void deleteById(String id);
}
