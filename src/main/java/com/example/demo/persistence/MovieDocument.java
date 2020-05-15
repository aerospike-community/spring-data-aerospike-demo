package com.example.demo.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Value // <1>
@Document(collection = "demo-service-movies") // <2>
@Builder(toBuilder = true) // <3>
// Spring Data object creation can use all-args constructor instead of reflection which is much faster
@AllArgsConstructor // <4>
public class MovieDocument {

    @Id // <5>
    String id;

    @Field // <6>
    String name;

    @Field("desc")  // <7>
    String description;

    @Field
    double rating;

    @Version // <8>
    @NonFinal
    long version;
}