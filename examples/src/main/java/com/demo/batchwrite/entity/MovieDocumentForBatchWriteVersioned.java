package com.demo.batchwrite.entity;

import com.aerospike.client.query.IndexCollectionType;
import com.aerospike.client.query.IndexType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.data.aerospike.annotation.Indexed;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;

@Data
@Document(collection = "demo-batchWrite-set-versioned")
@Builder(toBuilder = true)
// Spring Data object creation can use all-args constructor instead of reflection which is much faster
public class MovieDocumentForBatchWriteVersioned {

    @Id
    String id;

    @Version
    int version;

    @Field
    String name;

    @Field("desc")
    String description;

    @Indexed(type = IndexType.NUMERIC, collectionType = IndexCollectionType.DEFAULT)
    @Field
    int likes;

    @Field
    double rating;

    public MovieDocumentForBatchWriteVersioned(String id, int version, String name, String description, int likes,
                                               double rating) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.description = description;
        this.likes = likes;
        this.rating = rating;
    }

    @PersistenceCreator
    public MovieDocumentForBatchWriteVersioned(String id, String name, String description, int likes, double rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.likes = likes;
        this.rating = rating;
    }
}
