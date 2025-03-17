package com.demo.reactive.simplecrud.repository;

import com.demo.reactive.simplecrud.entity.MovieDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveMovieRepository extends ReactiveAerospikeRepository<MovieDocument, String> {

}
