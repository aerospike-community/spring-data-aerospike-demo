//package com.example.demo;
//
//import com.example.demo.persistence.service.Movie;
//import com.example.demo.persistence.service.MovieOperations;
//import com.playtika.testcontainer.aerospike.AerospikeTestOperations;
//import lombok.SneakyThrows;
//import org.assertj.core.util.Lists;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//import java.util.concurrent.*;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//public class MovieOperationsTests {
//
//    @Autowired
//    AerospikeTestOperations testOperations;
//
//    @Autowired
//    MovieOperations movieOperations;
//
//    @Test
//    void createsAndDeletesMovie() {
//        Movie movie = new Movie("Back to the Future", "Student is accidentally sent thirty years into the past.", 8.5);
//
//        movieOperations.createMovie(movie);
//
//        assertThat(movieOperations.findMovie(movie.getName())).isPresent();
//
//        movieOperations.deleteMovie(movie.getName());
//
//        assertThat(movieOperations.findMovie(movie.getName())).isNotPresent();
//    }
//
//    @Test
//    void createMovie_throwsExceptionForDuplicateMovie() {
//        Movie movie = new Movie("The Shawshank Redemption",
//                "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
//                9.3);
//
//        movieOperations.createMovie(movie);
//
//        assertThatThrownBy(() -> movieOperations.createMovie(movie))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("Movie with name: The Shawshank Redemption already exists!");
//    }
//
//    @Test
//    void update_updatesData() {
//        Movie movie = new Movie("Casablanca",
//                "A cynical ??? expatriate struggles to decide whether or not he should help his former lover and her fugitive husband escape French Morocco.",
//                0.0);
//
//        movieOperations.createMovie(movie);
//
//        movieOperations.updateMovieDescription(movie.getName(), "A cynical American expatriate struggles to decide whether or not he should help his former lover and her fugitive husband escape French Morocco.");
//        movieOperations.updateMovieRating(movie.getName(), 8.5);
//
//        assertThat(movieOperations.findMovie(movie.getName()))
//                .hasValue(new Movie("Casablanca",
//                        "A cynical American expatriate struggles to decide whether or not he should help his former lover and her fugitive husband escape French Morocco.",
//                        8.5));
//    }
//
//    @Test
//    void update_concurrentlyUpdatesData() throws InterruptedException {
//        Movie movie = new Movie("Very old movie",
//                "Old description",
//                0.0);
//
//        movieOperations.createMovie(movie);
//        assertThat(movieOperations.findMovie(movie.getName())).hasValue(
//                new Movie(movie.getName(), "Old description", 0.0)
//        );
//
//        ExecutorService pool = Executors.newFixedThreadPool(2);
//        CountDownLatch latch = new CountDownLatch(2);
//        List<Callable<Movie>> tasks = Lists.list(
//                () -> {
//                    latch.countDown();
//                    latch.await();
//                    return movieOperations.updateMovieRating(movie.getName(), 5.5);
//                },
//                () -> {
//                    latch.countDown();
//                    latch.await();
//                    return movieOperations.updateMovieDescription(movie.getName(), "New description");
//                }
//        );
//        pool.invokeAll(tasks)
//                .forEach(this::waitUntilDone);
//
//        assertThat(movieOperations.findMovie(movie.getName())).hasValue(
//                new Movie(movie.getName(), "New description", 5.5)
//        );
//    }
//
//    @SneakyThrows
//    private Movie waitUntilDone(Future<Movie> task) {
//        return task.get(5, TimeUnit.SECONDS);
//    }
//}
