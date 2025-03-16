package com.demo.reactive.customconverters.entity;

import lombok.Value;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;
import org.springframework.data.annotation.Id;

@Value
@Document
public class UserDocument {

    @Id
    long id;

    @Field
    UserData data;

    public record UserData(String address, String country) {
    }
}
