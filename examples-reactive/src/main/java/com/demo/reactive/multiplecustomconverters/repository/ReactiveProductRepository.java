package com.demo.reactive.multiplecustomconverters.repository;

import com.demo.reactive.multiplecustomconverters.entity.ProductDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveProductRepository extends ReactiveAerospikeRepository<ProductDocument, String> {

}
