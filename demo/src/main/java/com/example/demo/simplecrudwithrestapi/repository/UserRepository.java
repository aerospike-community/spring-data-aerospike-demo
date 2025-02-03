package com.example.demo.simplecrudwithrestapi.repository;

import com.example.demo.simplecrudwithrestapi.entity.User;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface UserRepository extends AerospikeRepository<User, Integer> {
}
