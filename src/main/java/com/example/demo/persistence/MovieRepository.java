package com.example.demo.persistence;

import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<MovieDocument, String> {
}
