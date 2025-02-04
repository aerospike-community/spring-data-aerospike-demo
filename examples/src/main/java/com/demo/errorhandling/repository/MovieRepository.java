package com.demo.errorhandling.repository;

import com.demo.errorhandling.entity.MovieDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface MovieRepository extends AerospikeRepository<MovieDocument, String> {
}
