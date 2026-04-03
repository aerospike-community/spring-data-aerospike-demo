package com.demo.multiplecustomconverters.repository;

import com.demo.multiplecustomconverters.entity.ProductDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface ProductRepository extends AerospikeRepository<ProductDocument, String> {

}
