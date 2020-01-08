package com.example.demo.service;

import com.example.demo.persistence.MovieDocument;
import com.example.demo.persistence.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Optional;
import java.util.function.Function;

@Retryable(
        include = {
                QueryTimeoutException.class,
                TransientDataAccessResourceException.class,
                OptimisticLockingFailureException.class
        },
        maxAttempts = 5,
        backoff = @Backoff(
                delay = 3,
                multiplier = 2,
                random = true
        )
)
@RequiredArgsConstructor
public class MovieOperations {

    private final MovieRepository repository;
    private final AerospikeTemplate template;

    public void createMovie(Movie movie) {
        try {
            template.insert(MovieDocument.builder()
                    .id(movie.getName())
                    .name(movie.getName())
                    .description(movie.getDescription())
                    .rating(movie.getRating())
                    .version(0L)
                    .build());
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Movie with name: " + movie.getName() + " already exists!");
        }
    }

    public void deleteMovie(String name) {
        repository.deleteById(name);
    }

    public Optional<Movie> findMovie(String name) {
        return repository.findById(name)
                .map(this::toMovie);
    }

    public Movie updateMovieRating(String name, double newRating) {
        return update(name, existingMovie ->
                repository.save(existingMovie.toBuilder().rating(newRating).build()));
    }

    public Movie updateMovieDescription(String name, String newDescription) {
        return update(name, existingMovie ->
                repository.save(existingMovie.toBuilder().description(newDescription).build()));
    }

    private Movie update(String name, Function<MovieDocument, MovieDocument> updateFunction) {
        return repository.findById(name)
                .map(updateFunction)
                .map(this::toMovie)
                .orElseThrow(() -> new IllegalArgumentException("Movie with name: " + name + " not found"));
    }

    private Movie toMovie(MovieDocument doc) {
        return new Movie(doc.getName(), doc.getDescription(), doc.getRating());
    }
}