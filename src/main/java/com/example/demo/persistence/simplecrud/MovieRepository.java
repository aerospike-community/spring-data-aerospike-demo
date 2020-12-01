package com.example.demo.persistence.simplecrud;

import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<MovieDocument, String> { // <1>
}
