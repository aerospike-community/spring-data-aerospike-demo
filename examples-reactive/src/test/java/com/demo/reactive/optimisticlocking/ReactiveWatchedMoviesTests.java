package com.demo.reactive.optimisticlocking;

import com.demo.reactive.optimisticlocking.service.ReactiveWatchedMoviesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveWatchedMoviesTests extends ReactiveOptimisticLockingAerospikeDemoApplicationTest {

    @Autowired
    ReactiveWatchedMoviesService watchedMoviesService;

    @Test
    void getWatchedMovies_returnsEmptyListIfDocumentIsNotPresent() {
        String id = "watched::" + UUID.randomUUID();

        StepVerifier.create(watchedMoviesService.getWatchedMovies(id))
                .assertNext(watchedMovies -> {
                    assertThat(watchedMovies).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void addWatchedMovie_insertsNewWatchedMovie() {
        String id = "watched::" + UUID.randomUUID();
        String watchedMovie = "Charlie and the Chocolate Factory";

        Mono<List<String>> addAndGetMovie = watchedMoviesService.addWatchedMovie(id, watchedMovie)
                        .then(watchedMoviesService.getWatchedMovies(id));

        StepVerifier.create(addAndGetMovie)
                .assertNext(results -> {
                    assertThat(results).containsOnly(watchedMovie);
                })
                .verifyComplete();
    }

    @Test
    void addWatchedMovie_updatesExistingWatchedMovies() {
        String id = "watched::" + UUID.randomUUID();
        String initialWatchedMovie = "Charlie and the Chocolate Factory";
        String newWatchedMovie = "The Tourist";

        Mono<List<String>> addAndGetMovie = watchedMoviesService.addWatchedMovie(id, initialWatchedMovie)
                .then(watchedMoviesService.addWatchedMovie(id, newWatchedMovie))
                .then(watchedMoviesService.getWatchedMovies(id));

        StepVerifier.create(addAndGetMovie)
                .assertNext(results -> {
                    assertThat(results).containsOnly(initialWatchedMovie, newWatchedMovie);
                })
                .verifyComplete();
    }
}
