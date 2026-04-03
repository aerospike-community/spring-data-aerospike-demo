package com.demo.reactive.nettyeventloops.repository;

import com.demo.reactive.nettyeventloops.entity.MovieDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveMovieRepository extends ReactiveAerospikeRepository<MovieDocument, String> {

}
