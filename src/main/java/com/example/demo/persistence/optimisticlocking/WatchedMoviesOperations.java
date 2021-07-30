package com.example.demo.persistence.optimisticlocking;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class WatchedMoviesOperations {

    private final WatchedMoviesDocumentRepository repository;

    @Retryable( // <1>
            include = { // <2>
                    QueryTimeoutException.class,
                    TimeoutException.class,
                    TransientDataAccessResourceException.class,
                    OptimisticLockingFailureException.class // <3>
            },
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 3,
                    multiplier = 2,
                    random = true
            )
    )
    public void addWatchedMovie(String id, String newWatchedMovie) {
        WatchedMoviesDocument watchedMoviesDocument = repository.findById(id) // <4>
                .map(existingDocument -> updateExistingDocument(existingDocument, newWatchedMovie)) // <5>
                .orElseGet(() -> createNewDocumentWithMovie(id, newWatchedMovie)); // <6>

        repository.save(watchedMoviesDocument); // <7>
    }

    private WatchedMoviesDocument createNewDocumentWithMovie(String id, String newWatchedMovie) {
        return WatchedMoviesDocument.builder()
                .key(id)
                .watchedMovie(newWatchedMovie)
                .build();
    }

    private WatchedMoviesDocument updateExistingDocument(WatchedMoviesDocument existingDocument, String newWatchedMovie) {
        // NOTE: we do not create new document here, but only update existing while retaining the version
        return existingDocument.toBuilder()
                .watchedMovie(newWatchedMovie)
                .build();
    }

    public List<String> getWatchedMovies(String id) {
        return repository.findById(id)
                .map(WatchedMoviesDocument::getWatchedMovies)
                .orElseGet(Collections::emptyList);
    }

}
