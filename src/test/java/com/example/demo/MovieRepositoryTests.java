package com.example.demo;

import com.example.demo.persistence.simplecrud.MovieDocument;
import com.example.demo.persistence.simplecrud.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieRepositoryTests extends DemoApplicationTests {

    String id;

    MovieDocument movie;

    @Autowired
    MovieRepository repository;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID().toString();
        movie = MovieDocument.builder()
                .id(id)
                .name("Inception")
                .description("Origin of an idea")
                .rating(8.8)
                .likes(555_555)
                .build();
    }

    @Test
    public void save_savesMovie() {
        repository.save(movie);

        assertThat(repository.findById(id)).hasValue(movie);
    }

    @Test
    public void exists_returnsTrueIfMovieIsPresent() {
        repository.save(movie);

        assertThat(repository.existsById(id)).isTrue();
    }

    @Test
    public void deleteById_deletesExistingMovie() {
        repository.save(movie);

        repository.deleteById(id);

        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    void deleteById_doesNothingForNonexistingMovie() {
        repository.deleteById(id);
    }
}