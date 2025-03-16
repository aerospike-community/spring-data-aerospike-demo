package com.demo.optimisticlocking;

import com.demo.optimisticlocking.service.WatchedMoviesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WatchedMoviesTests extends OptimisticLockingAerospikeDemoApplicationTest {

    @Autowired
    WatchedMoviesService watchedMoviesService;

    @Test
    void getWatchedMovies_returnsEmptyListIfDocumentIsNotPresent() {
        String id = "watched::" + UUID.randomUUID();

        List<String> watchedMovies = watchedMoviesService.getWatchedMovies(id);
        assertThat(watchedMovies).isEmpty();
    }

    @Test
    void addWatchedMovie_insertsNewWatchedMovie() {
        String id = "watched::" + UUID.randomUUID();
        String watchedMovie = "Charlie and the Chocolate Factory";

        watchedMoviesService.addWatchedMovie(id, watchedMovie);
        assertThat(watchedMoviesService.getWatchedMovies(id)).containsOnly(watchedMovie);
    }

    @Test
    void addWatchedMovie_updatesExistingWatchedMovies() {
        String id = "watched::" + UUID.randomUUID();
        String initialWatchedMovie = "Charlie and the Chocolate Factory";
        String newWatchedMovie = "The Tourist";

        watchedMoviesService.addWatchedMovie(id, initialWatchedMovie);
        assertThat(watchedMoviesService.getWatchedMovies(id)).containsOnly(initialWatchedMovie);

        watchedMoviesService.addWatchedMovie(id, newWatchedMovie);
        assertThat(watchedMoviesService.getWatchedMovies(id)).containsOnly(initialWatchedMovie, newWatchedMovie);
    }
}
