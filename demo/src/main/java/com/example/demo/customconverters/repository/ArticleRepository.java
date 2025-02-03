package com.example.demo.customconverters.repository;

import com.example.demo.customconverters.entity.ArticleDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface ArticleRepository extends AerospikeRepository<ArticleDocument, String> {
}
