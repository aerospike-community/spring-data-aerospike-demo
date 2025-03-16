package com.demo.optimisticlocking;

import com.demo.optimisticlocking.service.WatchedMoviesService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public class WatchedMoviesConcurrentTests extends OptimisticLockingAerospikeDemoApplicationTest {

    private final int NUMBER_OF_TASKS = 10;
    private final CountDownLatch latch = new CountDownLatch(1);

    private ExecutorService executor;

    @Autowired
    WatchedMoviesService watchedMoviesService;

    @BeforeEach
    public void before() {
        int NUMBER_OF_THREADS = 5;
        executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    @AfterEach
    public void after() {
        executor.shutdownNow();
    }

    @Test
    void addWatchedMovie_addsMoviesConcurrently() throws Exception {
        String id = "watched::" + UUID.randomUUID();
        runTasksAndWaitForCompletion(() -> watchedMoviesService.addWatchedMovie(id, "Movie " + UUID.randomUUID()));

        List<String> watchedMovies = watchedMoviesService.getWatchedMovies(id);
        assertThat(watchedMovies).hasSize(NUMBER_OF_TASKS);
    }

    @RequiredArgsConstructor
    private class LatchAwareTask implements Runnable {

        private final Runnable task;

        @Override
        public void run() {
            try {
                latch.await();
                task.run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void runTasksAndWaitForCompletion(Runnable runnable) throws Exception {
        // submit tasks
        CompletableFuture[] futures = range(0, NUMBER_OF_TASKS)
                .mapToObj(index -> new LatchAwareTask(runnable))
                .map(task -> CompletableFuture.runAsync(task, executor))
                .toArray(CompletableFuture[]::new);

        // start all tasks simultaneously
        latch.countDown();

        // wait for completion
        CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS);
    }
}
