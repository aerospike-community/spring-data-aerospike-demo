package com.demo.simplecrud;

import com.demo.simplecrud.entity.MovieDocument;
import com.demo.simplecrud.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieRepositoryTests extends SimpleCrudAerospikeDemoApplicationTest {

    String id;
    MovieDocument movie;

    @Autowired
    MovieRepository repository;

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
        repository.save(movie);
        assertThat(repository.findById(id)).hasValue(movie);
    }

    @Test
    public void exists_returnsTrueIfMovieIsPresent() {
        repository.save(movie);
        assertThat(repository.existsById(id)).isTrue();
    }

    @Test
    public void deleteExistingMovieById() {
        repository.save(movie);
        repository.deleteById(id);
        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    void deleteById_skipsNonExistingMovie() {
        repository.deleteById(id);
        repository.deleteById(id);
        assertThat(repository.findById(id)).isNotPresent();

        repository.save(movie);
        repository.deleteById("testId");
        assertThat(repository.findById(id)).isPresent();
        assertThat(repository.findById(id).get()).isEqualTo(movie);
    }
}
