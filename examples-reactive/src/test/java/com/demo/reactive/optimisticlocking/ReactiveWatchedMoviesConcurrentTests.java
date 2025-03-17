package com.demo.reactive.optimisticlocking;

import com.demo.reactive.optimisticlocking.entity.WatchedMoviesDocument;
import com.demo.reactive.optimisticlocking.service.ReactiveWatchedMoviesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReactiveWatchedMoviesConcurrentTests extends ReactiveOptimisticLockingAerospikeDemoApplicationTest {

    private final int NUMBER_OF_TASKS = 10;

    @Autowired
    ReactiveWatchedMoviesService watchedMoviesService;

    @Test
    void addWatchedMovie_addsMoviesConcurrently() {
        String id = "watched::" + UUID.randomUUID();

        // Set up the parallel task execution
        Flux<WatchedMoviesDocument> results = Flux.range(0, NUMBER_OF_TASKS)
                .flatMap(i ->
                                watchedMoviesService.addWatchedMovie(id, "Movie " + UUID.randomUUID())
                                        .doOnSuccess(doc -> System.out.println("Added movie " + i + ", version: " + doc.getVersion()))
                                        .doOnError(e -> System.err.println("Error for movie " + i + ": " + e.getMessage())),
                        // Run with higher concurrency to properly test the retry logic
                        5
                );

        // Execute all operations and wait for completion
        StepVerifier.create(results.collectList())
                .assertNext(list -> {
                    System.out.println("Successfully completed " + list.size() + " operations");
                    assertThat(list).hasSize(NUMBER_OF_TASKS);
                })
                .verifyComplete();

        // Verify all movies were added
        StepVerifier.create(watchedMoviesService.getWatchedMovies(id))
                .assertNext(movies -> {
                    System.out.println("Found " + movies.size() + " movies");
                    assertThat(movies).hasSize(NUMBER_OF_TASKS);
                })
                .verifyComplete();
    }
}