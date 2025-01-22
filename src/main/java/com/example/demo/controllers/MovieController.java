package com.example.demo.controllers;

import com.example.demo.service.Movie;
import com.example.demo.service.MovieOperations;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class MovieController {

    MovieOperations movieService;

    @GetMapping("/movies/{name}")
    public Optional<Movie> findMovieByName(@PathVariable("name") String movieName) {
        return movieService.findMovie(movieName);
    }

    @PostMapping("/movies")
    public void addMovie(@RequestBody Movie movie) {
        movieService.createMovie(movie);
    }

    @DeleteMapping("/movies/{name}")
    public void deleteMovieByName(@PathVariable("name") String movieName) {
        movieService.deleteMovie(movieName);
    }
}
