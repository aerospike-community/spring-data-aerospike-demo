package com.example.demo.persistence.simplecrud;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class MovieController {

    MovieService movieService;

    @GetMapping("/users/{id}")
    public Optional<MovieDocument> readUserById(@PathVariable("id") String id) {
        return movieService.readMovieById(id);
    }

    @PostMapping("/users")
    public void addUser(@RequestBody MovieDocument movie) {
        movieService.addMovie(movie);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable("id") String id) {
        movieService.removeMovieById(id);
    }
}
