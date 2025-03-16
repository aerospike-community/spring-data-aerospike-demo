package com.demo.reactive.errorhandling;

import com.demo.reactive.errorhandling.entity.Movie;
import com.demo.reactive.errorhandling.entity.MovieDocument;
import com.demo.reactive.errorhandling.service.ReactiveMovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class MovieServiceTests extends ReactiveErrorHandlingAerospikeDemoApplicationTest {

    @Autowired
    ReactiveMovieService movieService;

    @Test
    void createsAndDeletesMovie() {
        Movie movie = new Movie("Back to the Future",
                "Student is accidentally sent thirty years into the past", 9.3);

        // Create the movie
        StepVerifier.create(movieService.createMovie(movie))
                .expectNextMatches(createdMovie -> createdMovie.getName().equals(movie.name()))
                .verifyComplete();

        // Find the movie and verify it exists
        StepVerifier.create(movieService.findMovie(movie.name()))
                .expectNextMatches(foundMovie -> foundMovie.name().equals(movie.name()))
                .verifyComplete();

        // Delete the movie
        StepVerifier.create(movieService.deleteMovie(movie.name()))
                .verifyComplete();

        // Try to find the movie again and verify it's not present
        StepVerifier.create(movieService.findMovie(movie.name()))
                .expectComplete()
                .verify();
    }
    @Test
    void createMovie_throwsExceptionForDuplicateMovie() {
        Movie movie = new Movie("Movie1", "Description1", 10);

        // First attempt to create the movie should succeed
        StepVerifier.create(movieService.createMovie(movie))
                .expectNextMatches(createdMovie -> createdMovie.getName().equals(movie.name()))
                .verifyComplete();

        // Second attempt to create the same movie should result in an error
        StepVerifier.create(movieService.createMovie(movie))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Movie with name: Movie1 already exists!"))
                .verify();
    }

    @Test
    void update_updatesData() {
        Movie initialMovie = new Movie("Casablanca",
                "A cynical American expatriate ??? to decide whether or not he should help his former lover " +
                        "and her fugitive husband escape French Morocco",
                0.0);
        String updatedDescription = "A cynical American expatriate struggles to decide " +
                "whether or not he should help his former lover and her fugitive husband escape French Morocco";
        double updatedRating = 8.5;
        Movie expectedMovie = new Movie("Casablanca", updatedDescription, updatedRating);

        // Create the movie
        Mono<MovieDocument> creationMono = movieService.createMovie(initialMovie);

        // Update the description
        Mono<Movie> updateDescriptionMono = creationMono.then(movieService.updateMovieDescription(initialMovie.name(),
                updatedDescription));

        // Update the rating
        Mono<Movie> updateRatingMono = updateDescriptionMono.then(movieService.updateMovieRating(initialMovie.name(),
                updatedRating));

        // Find the movie and verify the updated data
        StepVerifier.create(updateRatingMono.then(movieService.findMovie(initialMovie.name())))
                .expectNextMatches(foundMovie -> foundMovie.equals(expectedMovie))
                .verifyComplete();
    }

    @Test
    void update_concurrentlyUpdatesData() {
        Movie movie = new Movie("Very old movie",
                "Old description",
                0.0);

        // Create the movie
        StepVerifier.create(movieService.createMovie(movie))
                .expectNextCount(1)
                .verifyComplete();

        // Verify it exists with expected initial values
        StepVerifier.create(movieService.findMovie(movie.name()))
                .expectNext(new Movie(movie.name(), "Old description", 0.0))
                .verifyComplete();

        // Set up the concurrent updates with retry logic
        // Retry  if optimistic locking failure occurs
        Mono<Movie> updateRating = movieService.updateMovieRating(movie.name(), 5.5);

        // Retry if optimistic locking failure occurs
        Mono<Movie> updateDescription = movieService.updateMovieDescription(movie.name(), "New description");

        // Create a countdown latch equivalent with Semaphore for synchronization
        CountDownLatch latch = new CountDownLatch(2);

        // Execute both updates in parallel, synchronized to start at the same time
        Mono<Movie> ratingTask = Mono.fromCallable(() -> {
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw Exceptions.propagate(e);
                    }
                    return true;
                }).flatMap(__ -> updateRating)
                .subscribeOn(Schedulers.boundedElastic());

        Mono<Movie> descriptionTask = Mono.fromCallable(() -> {
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw Exceptions.propagate(e);
                    }
                    return true;
                }).flatMap(__ -> updateDescription)
                .subscribeOn(Schedulers.boundedElastic());

        // Wait for both tasks to complete
        StepVerifier.create(Mono.zip(ratingTask, descriptionTask).timeout(Duration.ofSeconds(5)))
                .expectNextCount(1)
                .verifyComplete();

        // Verify the final state includes both updates
        StepVerifier.create(movieService.findMovie(movie.name()))
                .expectNext(new Movie(movie.name(), "New description", 5.5))
                .verifyComplete();
    }
}
