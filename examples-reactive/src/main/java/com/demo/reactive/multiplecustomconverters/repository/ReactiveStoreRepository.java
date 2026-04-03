package com.demo.reactive.multiplecustomconverters.repository;

import com.demo.reactive.multiplecustomconverters.entity.StoreDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveStoreRepository extends ReactiveAerospikeRepository<StoreDocument, String> {

}
