package com.example.demo.compositeprimarykey;

import com.example.demo.compositeprimarykey.entity.CommentsDocument;
import com.example.demo.compositeprimarykey.entity.CommentsKey;
import com.example.demo.compositeprimarykey.repository.AerospikeCommentsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CommentsRepositoryTest extends CompositePKAerospikeDemoApplicationTest {

    @Autowired
    AerospikeCommentsRepository repository;

    @Test
    void getAndSaveComments() {
        CommentsKey key = new CommentsKey(1L, 7624L);
        List<String> comments = List.of("Great movie", "ONE OF THE BEST FILMS EVER MADE");
        CommentsDocument document = new CommentsDocument(key, comments);
        // custom converters are registered via customConverters method in configuration
        repository.save(document);

        Optional<CommentsDocument> actual = repository.findById(key);
        assertThat(actual).hasValue(document);
    }
}