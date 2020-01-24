package com.example.demo.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Value
@Document(collection = "demo-service-movies")
@Builder(toBuilder = true)
@AllArgsConstructor // Spring Data object creation can use all-args constructor instead of reflection which is much faster
public class MovieDocument {

    @Id
    String id;

    @Field
    String name;

    @Field
    String description;

    @Field
    double rating;

    @Version
    @NonFinal
    long version;
}
