package com.example.demo.persistence.index;

import org.springframework.data.repository.CrudRepository;

public interface IndexedDocumentRepository extends CrudRepository<IndexedDocument, String> {
}
