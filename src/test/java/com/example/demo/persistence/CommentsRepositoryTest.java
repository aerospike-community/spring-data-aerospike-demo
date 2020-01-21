package com.example.demo.persistence;

import com.example.demo.DemoApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CommentsRepositoryTest extends DemoApplicationTests {

    @Autowired
    CommentsRepository repository;

    @Test
    void getAndSaveComments() {
        CommentsKey key = new CommentsKey(1L, 7624L);
        List<String> comments = List.of("Great movie", "ONE OF THE BEST FILMS EVER MADE");
        CommentsDocument document = new CommentsDocument(key, comments);
        repository.save(document);

        Optional<CommentsDocument> actual = repository.findById(key);

//        assertThat(actual).hasValue(document);
        assertThat(actual).isEmpty();//TODO: will be fixed in https://github.com/aerospike-community/spring-data-aerospike/pull/66
    }
}