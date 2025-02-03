package com.example.demo.optimisticlocking;

import com.example.demo.optimisticlocking.service.WatchedMoviesOperations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WatchedMoviesTest extends OptimisticLockingAerospikeDemoApplicationTest {

    @Autowired
    WatchedMoviesOperations watchedMoviesOperations;

    @Test
    void getWatchedMovies_returnsEmptyListIfDocumentIsNotPresent() {
        String id = "watched::" + UUID.randomUUID();

        List<String> watchedMovies = watchedMoviesOperations.getWatchedMovies(id);
        assertThat(watchedMovies).isEmpty();
    }

    @Test
    void addWatchedMovie_insertsNewWatchedMovie() {
        String id = "watched::" + UUID.randomUUID();
        String watchedMovie = "Charlie and the Chocolate Factory";

        watchedMoviesOperations.addWatchedMovie(id, watchedMovie);
        assertThat(watchedMoviesOperations.getWatchedMovies(id)).containsOnly(watchedMovie);
    }

    @Test
    void addWatchedMovie_updatesExistingWatchedMovies() {
        String id = "age::" + UUID.randomUUID();
        String initialWatchedMovie = "Charlie and the Chocolate Factory";
        String newWatchedMovie = "The Tourist";

        watchedMoviesOperations.addWatchedMovie(id, initialWatchedMovie);
        assertThat(watchedMoviesOperations.getWatchedMovies(id)).containsOnly(initialWatchedMovie);

        watchedMoviesOperations.addWatchedMovie(id, newWatchedMovie);
        assertThat(watchedMoviesOperations.getWatchedMovies(id)).containsOnly(initialWatchedMovie, newWatchedMovie);
    }
}