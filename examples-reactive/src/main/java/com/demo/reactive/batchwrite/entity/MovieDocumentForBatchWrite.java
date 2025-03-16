package com.demo.reactive.batchwrite.entity;

import com.aerospike.client.query.IndexCollectionType;
import com.aerospike.client.query.IndexType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.aerospike.annotation.Indexed;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;

@Value
@Document(collection = "demo-batchWrite-reactive-set")
@Builder(toBuilder = true)
// Spring Data object creation can use all-args constructor instead of reflection which is much faster
@AllArgsConstructor
public class MovieDocumentForBatchWrite {

    @Id
    String id;

    @Field
    String name;

    @Field("desc")
    String description;

    @Indexed(type = IndexType.NUMERIC, collectionType = IndexCollectionType.DEFAULT)
    @Field
    int likes;

    @Field
    double rating;
}
