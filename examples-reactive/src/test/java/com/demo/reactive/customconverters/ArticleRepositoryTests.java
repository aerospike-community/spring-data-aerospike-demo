package com.demo.reactive.customconverters;

import com.demo.reactive.customconverters.converter.ArticleDocumentConverters;
import com.demo.reactive.customconverters.entity.ArticleDocument;
import com.demo.reactive.customconverters.repository.ReactiveArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.UUID;

public class ArticleRepositoryTests extends ReactiveCustomConvertersAerospikeDemoApplicationTest {

    @Autowired
    ReactiveArticleRepository repository;

    /**
     * see {@link ArticleDocumentConverters}
     */
    @Test
    void savesAndReadsDataUsingCustomConverters() {
        String id = UUID.randomUUID().toString();
        ArticleDocument document = new ArticleDocument(id, "Anastasiia Smirnova", "The content of the article", true);

        StepVerifier.create(repository.save(document))
                .expectNext(document)
                .verifyComplete();

        StepVerifier.create(repository.findById(id))
                .expectNext(document)
                .verifyComplete();
    }

    @Test
    void expiresDraftAccordingToConfiguration() throws InterruptedException {
        String id = UUID.randomUUID().toString();
        ArticleDocument document = new ArticleDocument(id, "Anastasiia Smirnova", "The content of the article", true);

        StepVerifier.create(repository.save(document))
                .expectNext(document)
                .verifyComplete();

        Thread.sleep(11000);
        // Wait for 11 seconds (to exceed the 10-second expiration
        // specified in ArticleDocumentToAerospikeWriteDataConverter)

        StepVerifier.create(repository.findById(id))
                .expectComplete();
    }
}
