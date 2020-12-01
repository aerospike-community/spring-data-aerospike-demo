package com.example.demo.persistence.indexed;

import org.springframework.data.repository.CrudRepository;

public interface IndexedDocumentRepository extends CrudRepository<IndexedDocument, String> {
}
