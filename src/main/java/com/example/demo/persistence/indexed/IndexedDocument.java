package com.example.demo.persistence.indexed;

import lombok.Value;
import org.springframework.data.aerospike.annotation.Indexed;
import org.springframework.data.aerospike.mapping.Document;

import java.util.List;

import static com.aerospike.client.query.IndexCollectionType.DEFAULT;
import static com.aerospike.client.query.IndexCollectionType.LIST;
import static com.aerospike.client.query.IndexType.NUMERIC;
import static com.aerospike.client.query.IndexType.STRING;

@Value
@Document
public class IndexedDocument {

    String key;

    @Indexed(type = STRING, collectionType = DEFAULT)
    String author;

    @Indexed(type = NUMERIC, collectionType = DEFAULT)
    int likes;

    @Indexed(type = NUMERIC, collectionType = LIST)
    List<Integer> options;
}
