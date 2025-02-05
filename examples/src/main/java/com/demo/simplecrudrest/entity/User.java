package com.demo.simplecrudrest.entity;

import com.aerospike.client.query.IndexCollectionType;
import com.aerospike.client.query.IndexType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.aerospike.annotation.Indexed;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;

@Data
@Document
@AllArgsConstructor
public class User {
    @Id
    private int id;
    @Indexed(type = IndexType.STRING, collectionType = IndexCollectionType.DEFAULT) // <8>
    private String name;
    private String email;
    private int age;
}
