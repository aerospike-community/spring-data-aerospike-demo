package com.demo.reactive.compositeprimarykey;

import com.demo.reactive.compositeprimarykey.entity.CommentsDocument;
import com.demo.reactive.compositeprimarykey.entity.CommentsKey;
import com.demo.reactive.compositeprimarykey.repository.ReactiveCommentsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

class CommentsRepositoryTests extends ReactiveCompositePKAerospikeDemoApplicationTest {

    @Autowired
    ReactiveCommentsRepository repository;

    @Test
    void saveComments() {
        CommentsKey key = new CommentsKey(1L, 7624L);
        List<String> comments = List.of("Great movie", "ONE OF THE BEST FILMS EVER MADE");
        CommentsDocument document = new CommentsDocument(key, comments);
        // custom converters are registered via customConverters method in configuration
        repository.save(document);

        StepVerifier.create(repository.save(document))
                .expectNext(document)
                .verifyComplete();

        StepVerifier.create(repository.findById(key))
                .expectNext(document)
                .verifyComplete();
    }
}
