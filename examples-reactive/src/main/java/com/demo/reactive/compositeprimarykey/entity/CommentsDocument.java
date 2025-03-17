package com.demo.reactive.compositeprimarykey.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;

import java.util.List;

@Value
@Document(collection = "demo-service-comments-set")
@Builder(toBuilder = true)
@AllArgsConstructor
// Spring Data object creation can use all-args constructor instead of reflection which is much faster
public class CommentsDocument {

    @Id
    CommentsKey key;

    @Field
    List<String> comments;
}
