package com.demo.reactive.simplecrud;

import com.demo.reactive.simplecrud.entity.MovieDocument;
import com.demo.reactive.simplecrud.repository.ReactiveMovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReactiveMovieRepositoryTests extends ReactiveSimpleCrudAerospikeDemoApplicationTest {

    String id;
    MovieDocument movie;

    @Autowired
    ReactiveMovieRepository repository;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID().toString();
        movie = MovieDocument.builder()
                .id(id)
                .name("Back To the Future")
                .description("I finally invented something that works!")
                .rating(9.3)
                .likes(555_555)
                .build();
    }

    @Test
    public void saveMovie() {
        Mono<Tuple2<MovieDocument, MovieDocument>> saveAndFind = repository.save(movie)
                .flatMap(savedMovie -> repository.findById(id)
                        .map(foundMovie -> Tuples.of(savedMovie, foundMovie))
                );
        StepVerifier.create(saveAndFind)
                .expectNextMatches(tuple -> {
                    assertThat(tuple.getT1()).isEqualTo(tuple.getT2());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void exists_returnsTrueIfMovieIsPresent() {
        Mono<Boolean> saveAndExists = repository.save(movie)
                .flatMap(savedMovie -> repository.existsById(id));
        StepVerifier.create(saveAndExists)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void deleteExistingMovieById() {
        Mono<MovieDocument> saveAndDelete = repository.save(movie)
                .then(repository.deleteById(id))
                .then(repository.findById(id));
        StepVerifier.create(saveAndDelete)
                .expectComplete();
    }

    @Test
    void deleteById_skipsNonExistingMovie() {
        Mono<MovieDocument> deleteTwiceAndFind = repository.save(movie)
                .then(repository.deleteById(id))
                .then(repository.deleteById(id))
                .then(repository.findById(id));
        StepVerifier.create(deleteTwiceAndFind)
                .expectComplete();

        Mono<Boolean> deleteNonExisting = repository.save(movie)
                .then(repository.deleteById("testId"))
                .then(repository.findById(id))
                .map(result -> result.equals(movie));
        StepVerifier.create(deleteNonExisting)
                .expectNext(true)
                .verifyComplete();
    }
}
