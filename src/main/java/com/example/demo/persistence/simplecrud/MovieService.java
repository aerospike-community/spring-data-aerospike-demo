package com.example.demo.persistence.simplecrud;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MovieService {

    MovieRepository movieRepository;

    public Optional<MovieDocument> readMovieById(String id) {
        return movieRepository.findById(id);
    }

    public void addMovie(MovieDocument movie) {
        movieRepository.save(movie);
    }

    public void removeMovieById(String id) {
        movieRepository.deleteById(id);
    }
}
