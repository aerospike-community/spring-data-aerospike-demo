package com.demo.reactive.optimisticlocking.service;

import com.demo.reactive.optimisticlocking.entity.WatchedMoviesDocument;
import com.demo.reactive.optimisticlocking.repository.ReactiveWatchedMoviesDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class ReactiveWatchedMoviesService {

    private final ReactiveWatchedMoviesDocumentRepository repository;

    private WatchedMoviesDocument createNewDocumentWithMovie(String id, String newWatchedMovie) {
        return WatchedMoviesDocument.builder()
                .key(id)
                .watchedMovie(newWatchedMovie)
                .build();
    }

    private WatchedMoviesDocument updateExistingDocument(WatchedMoviesDocument existingDocument,
                                                         String newWatchedMovie) {
        // NOTE: we do not create new document here, but only update existing while retaining the version
        return existingDocument.toBuilder()
                .watchedMovie(newWatchedMovie) // add to the List
                .build();
    }

    public Mono<WatchedMoviesDocument> addWatchedMovie(String id, String newWatchedMovie) {
        // Define the entire operation as a function that can be retried
        Mono<WatchedMoviesDocument> addWatchedMovie = repository.findById(id)
                .flatMap(existingDocument -> {
                    WatchedMoviesDocument updatedDocument = updateExistingDocument(existingDocument, newWatchedMovie);
                    return repository.save(updatedDocument);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    WatchedMoviesDocument newDocument = createNewDocumentWithMovie(id, newWatchedMovie);
                    return repository.save(newDocument);
                }));

        // Apply retry to the entire operation
        return addWatchedMovie.retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                .jitter(0.5)
                .filter(e -> e instanceof QueryTimeoutException
                        || e instanceof TimeoutException
                        || e instanceof TransientDataAccessResourceException
                        || e instanceof OptimisticLockingFailureException)
                .multiplier(1.5)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        new IllegalStateException("Retries exhausted for addWatchedMovie: " + retrySignal.failure())));
    }

    public Mono<List<String>> getWatchedMovies(String id) {
        return repository.findById(id)
                .map(WatchedMoviesDocument::getWatchedMovies)
                .defaultIfEmpty(Collections.emptyList());
    }
}
