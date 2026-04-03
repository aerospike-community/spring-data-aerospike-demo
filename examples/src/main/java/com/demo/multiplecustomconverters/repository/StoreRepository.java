package com.demo.multiplecustomconverters.repository;

import com.demo.multiplecustomconverters.entity.StoreDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface StoreRepository extends AerospikeRepository<StoreDocument, String> {

}
