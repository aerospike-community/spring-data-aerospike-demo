package com.demo.simplecrudrest.repository;

import com.demo.simplecrudrest.entity.User;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface UserRepository extends AerospikeRepository<User, Integer> {
}
