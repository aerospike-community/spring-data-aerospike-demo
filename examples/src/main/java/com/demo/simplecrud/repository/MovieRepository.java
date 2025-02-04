package com.demo.simplecrud.repository;

import com.demo.simplecrud.entity.MovieDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface MovieRepository extends AerospikeRepository<MovieDocument, String> { // <1>
}
