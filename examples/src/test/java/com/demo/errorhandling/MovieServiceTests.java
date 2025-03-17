package com.demo.errorhandling;

import com.demo.errorhandling.entity.Movie;
import com.demo.errorhandling.service.MovieService;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MovieServiceTests extends ErrorHandlingAerospikeDemoApplicationTest {

    @Autowired
    MovieService movieService;

    @Test
    void createsAndDeletesMovie() {
        Movie movie = new Movie("Back to the Future",
                "Student is accidentally sent thirty years into the past", 9.3);

        movieService.createMovie(movie);
        assertThat(movieService.findMovie(movie.name())).isPresent();

        movieService.deleteMovie(movie.name());
        assertThat(movieService.findMovie(movie.name())).isNotPresent();
    }

    @Test
    void createMovie_throwsExceptionForDuplicateMovie() {
        Movie movie = new Movie("Movie1", "Description1", 10);
        movieService.createMovie(movie);
        assertThatThrownBy(() -> movieService.createMovie(movie))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Movie with name: Movie1 already exists!");
    }

    @Test
    void update_updatesData() {
        Movie movie = new Movie("Casablanca",
                "A cynical American expatriate ??? to decide whether or not he should help his former lover " +
                        "and her fugitive husband escape French Morocco",
                0.0);
        movieService.createMovie(movie);

        movieService.updateMovieDescription(movie.name(), "A cynical American expatriate struggles to decide " +
                "whether or not he should help his former lover and her fugitive husband escape French Morocco");
        movieService.updateMovieRating(movie.name(), 8.5);

        assertThat(movieService.findMovie(movie.name()))
                .hasValue(new Movie("Casablanca",
                        "A cynical American expatriate struggles to decide whether or not he should help " +
                                "his former lover and her fugitive husband escape French Morocco",
                        8.5));
    }

    @Test
    void update_concurrentlyUpdatesData() throws InterruptedException {
        Movie movie = new Movie("Very old movie",
                "Old description",
                0.0);

        movieService.createMovie(movie);
        assertThat(movieService.findMovie(movie.name())).hasValue(
                new Movie(movie.name(), "Old description", 0.0)
        );

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        List<Callable<Movie>> tasks = Lists.list(
                () -> {
                    latch.countDown();
                    latch.await();
                    return movieService.updateMovieRating(movie.name(), 5.5);
                },
                () -> {
                    latch.countDown();
                    latch.await();
                    return movieService.updateMovieDescription(movie.name(), "New description");
                }
        );
        pool.invokeAll(tasks)
                .forEach(this::waitUntilDone);

        assertThat(movieService.findMovie(movie.name())).hasValue(
                new Movie(movie.name(), "New description", 5.5)
        );
    }

    @SneakyThrows
    private void waitUntilDone(Future<Movie> task) {
        task.get(5, TimeUnit.SECONDS);
    }
}
