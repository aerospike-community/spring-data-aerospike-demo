package com.example.demo.customconverters.repository;

import com.example.demo.customconverters.entity.UserDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface UserRepository extends AerospikeRepository<UserDocument, Long> {
}
