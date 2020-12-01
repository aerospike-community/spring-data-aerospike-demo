package com.example.demo.persistence.customconverters;

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

    @Value
    public static class UserData {

        String address;
        String country;
    }
}
