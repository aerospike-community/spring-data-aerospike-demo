package com.demo.index.repository;

import com.demo.index.entity.IndexedDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface IndexedDocumentRepository extends AerospikeRepository<IndexedDocument, String> {

}
