package com.example.demo.persistence.customconverters;

import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleReactiveRepository extends ReactiveAerospikeRepository<ArticleDocument, String> {
}
