package com.demo.nettyeventloops.repository;

import com.demo.nettyeventloops.entity.MovieDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface MovieRepository extends AerospikeRepository<MovieDocument, String> {

}
