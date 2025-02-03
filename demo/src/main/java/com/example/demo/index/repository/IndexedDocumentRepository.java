package com.example.demo.index.repository;

import com.example.demo.index.entity.IndexedDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface IndexedDocumentRepository extends AerospikeRepository<IndexedDocument, String> {
}
