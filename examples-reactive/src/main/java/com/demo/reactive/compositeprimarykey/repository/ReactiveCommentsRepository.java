package com.demo.reactive.compositeprimarykey.repository;

import com.demo.reactive.compositeprimarykey.entity.CommentsDocument;
import com.demo.reactive.compositeprimarykey.entity.CommentsKey;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveCommentsRepository extends ReactiveAerospikeRepository<CommentsDocument, CommentsKey> {

}
