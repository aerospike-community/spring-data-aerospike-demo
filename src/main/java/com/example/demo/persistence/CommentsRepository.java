package com.example.demo.persistence;

import org.springframework.data.repository.CrudRepository;

public interface CommentsRepository extends CrudRepository<CommentsDocument, CommentsKey> {
}
