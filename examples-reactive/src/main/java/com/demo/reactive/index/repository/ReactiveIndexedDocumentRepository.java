package com.demo.reactive.index.repository;


import com.demo.reactive.index.entity.IndexedDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveIndexedDocumentRepository extends ReactiveAerospikeRepository<IndexedDocument, String> {

}
