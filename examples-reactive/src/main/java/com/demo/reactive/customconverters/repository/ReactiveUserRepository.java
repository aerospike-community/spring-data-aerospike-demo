package com.demo.reactive.customconverters.repository;

import com.demo.reactive.customconverters.entity.UserDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveUserRepository extends ReactiveAerospikeRepository<UserDocument, Long> {

}
