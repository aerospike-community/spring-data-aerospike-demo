package com.example.demo.persistence;

import lombok.Value;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.util.List;

@Value
@Document
public class MovieDocument {

    @Id
    String id;

    @Field
    String name;

    @Field
    String description;

    @Field
    double rating;

    @Field
    List<PersonDocument> stars;

    @Version
    long version;
}
