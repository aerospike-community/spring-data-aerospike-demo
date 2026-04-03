package com.demo.multiplecustomconverters.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;

@Value
@Document(collection = "demo-multiplecustomconverters-products")
@Builder(toBuilder = true)
@AllArgsConstructor
public class ProductDocument {

    @Id
    String id;

    @Field
    String name;

    @Field
    Price price;
}
