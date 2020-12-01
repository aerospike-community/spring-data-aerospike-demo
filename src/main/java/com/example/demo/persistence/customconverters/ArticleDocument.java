package com.example.demo.persistence.customconverters;

import lombok.Value;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;

@Value
@Document(collection = ArticleDocument.SET_NAME)
public class ArticleDocument {

    public static final String SET_NAME = "demo-service-articles";

    @Id
    String id;

    String author;

    String content;

    boolean draft;
}
