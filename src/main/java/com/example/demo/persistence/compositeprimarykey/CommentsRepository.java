package com.example.demo.persistence.compositeprimarykey;

import org.springframework.data.repository.CrudRepository;

public interface CommentsRepository extends CrudRepository<CommentsDocument, CommentsKey> {
}
