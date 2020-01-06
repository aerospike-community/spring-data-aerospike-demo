package com.example.demo;

import com.example.demo.persistence.MovieDocument;
import com.example.demo.persistence.MovieRepository;
import com.example.demo.persistence.PersonDocument;
import org.assertj.core.util.Lists;
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
        movie = new MovieDocument(id, "Inception",
                "Origin of an idea", 8.8,
                Lists.list(new PersonDocument("Leonardo DiCaprio"),
                        new PersonDocument("Joseph Gordon-Levitt"),
                        new PersonDocument("Ellen Page")), 0L);
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