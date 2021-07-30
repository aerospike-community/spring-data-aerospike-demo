package com.example.demo.persistence.optimisticlocking;

import org.springframework.data.repository.CrudRepository;

public interface WatchedMoviesDocumentRepository extends CrudRepository<WatchedMoviesDocument, String> {
}
