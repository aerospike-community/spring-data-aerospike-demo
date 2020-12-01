package com.example.demo.persistence.optimisticlocking;

import org.springframework.data.repository.CrudRepository;

public interface VersionedDocumentRepository extends CrudRepository<VersionedDocument, String> {
}
