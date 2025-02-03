package com.example.demo.compositeprimarykey.repository;

import com.example.demo.compositeprimarykey.entity.CommentsDocument;
import com.example.demo.compositeprimarykey.entity.CommentsKey;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface AerospikeCommentsRepository extends AerospikeRepository<CommentsDocument, CommentsKey> {
}
