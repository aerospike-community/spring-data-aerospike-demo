package com.demo.compositeprimarykey.repository;

import com.demo.compositeprimarykey.entity.CommentsDocument;
import com.demo.compositeprimarykey.entity.CommentsKey;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface CommentsRepository extends AerospikeRepository<CommentsDocument, CommentsKey> {

}
