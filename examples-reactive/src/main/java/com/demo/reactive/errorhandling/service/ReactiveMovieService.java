package com.demo.reactive.errorhandling.service;

import com.demo.reactive.errorhandling.entity.Movie;
import com.demo.reactive.errorhandling.entity.MovieDocument;
import com.demo.reactive.errorhandling.repository.ReactiveMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.aerospike.core.ReactiveAerospikeTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class ReactiveMovieService {

    private final ReactiveMovieRepository repository;
    private final ReactiveAerospikeTemplate template;

    public Mono<MovieDocument> createMovie(Movie movie) {
        return template.insert(MovieDocument.builder()
                        .id(movie.name())
                        .name(movie.name())
                        .description(movie.description())
                        .rating(movie.rating())
                        .version(0L)
                        .build())
                .onErrorMap(DuplicateKeyException.class,
                        e -> new IllegalArgumentException("Movie with name: " + movie.name() + " already exists!"));
    }

    public Mono<Void> deleteMovie(String name) {
        return repository.deleteById(name);
    }

    public Mono<Movie> findMovie(String name) {
        return repository.findById(name)
                .map(this::toMovie);
    }

    public Mono<Movie> updateMovieRating(String name, double newRating) {
        return Mono.defer(() -> update(name, existingMovie ->
                        repository.save(existingMovie.toBuilder().rating(newRating).build())))
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(3))
                        .jitter(0.5)
                        .filter(e -> e instanceof QueryTimeoutException
                                || e instanceof TransientDataAccessResourceException
                                || e instanceof OptimisticLockingFailureException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new IllegalStateException("Retries exhausted for updateMovieRating: " + retrySignal.failure())))
                .map(this::toMovie);
    }

    private Mono<MovieDocument> update(String name, Function<MovieDocument, Mono<MovieDocument>> updateFunction) {
        return repository.findById(name)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Movie with name: " + name + " not found")))
                .flatMap(updateFunction);
    }

    public Mono<Movie> updateMovieDescription(String name, String newDescription) {
        return Mono.defer(() -> update(name, existingMovie ->
                        repository.save(existingMovie.toBuilder().description(newDescription).build())))
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(3))
                        .jitter(0.5)
                        .filter(e -> e instanceof QueryTimeoutException
                                || e instanceof TransientDataAccessResourceException
                                || e instanceof OptimisticLockingFailureException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new IllegalStateException("Retries exhausted for updateMovieDescription: " + retrySignal.failure())))
                .map(this::toMovie);
    }

    private Movie toMovie(MovieDocument doc) {
        return new Movie(doc.getName(), doc.getDescription(), doc.getRating());
    }
}
