package com.demo.customconverters.repository;

import com.demo.customconverters.entity.ArticleDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface ArticleRepository extends AerospikeRepository<ArticleDocument, String> {

}
