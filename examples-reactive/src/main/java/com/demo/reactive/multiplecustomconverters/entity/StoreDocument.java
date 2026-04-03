package com.demo.reactive.multiplecustomconverters.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;

@Value
@Document(collection = "demo-reactive-multiplecustomconverters-stores")
@Builder(toBuilder = true)
@AllArgsConstructor
public class StoreDocument {

    @Id
    String id;

    @Field
    String name;

    @Field
    Address location;
}
