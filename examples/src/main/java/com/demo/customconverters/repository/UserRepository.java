package com.demo.customconverters.repository;

import com.demo.customconverters.entity.UserDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface UserRepository extends AerospikeRepository<UserDocument, Long> {
}
