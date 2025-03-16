package com.demo.reactive.customconverters.repository;


import com.demo.reactive.customconverters.entity.ArticleDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveArticleRepository extends ReactiveAerospikeRepository<ArticleDocument, String> {

}
