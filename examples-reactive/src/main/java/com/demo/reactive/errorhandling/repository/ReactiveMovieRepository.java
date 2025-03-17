package com.demo.reactive.errorhandling.repository;


import com.demo.reactive.errorhandling.entity.MovieDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveMovieRepository extends ReactiveAerospikeRepository<MovieDocument, String> {

}
