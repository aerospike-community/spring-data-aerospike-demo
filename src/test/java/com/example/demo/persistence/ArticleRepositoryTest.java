package com.example.demo.persistence;

import com.example.demo.DemoApplicationTests;
import com.example.demo.persistence.customconverters.ArticleDocument;
import com.example.demo.persistence.customconverters.ArticleDocumentConverters;
import com.example.demo.persistence.customconverters.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleRepositoryTest extends DemoApplicationTests {

    @Autowired
    ArticleRepository repository;

    /**
     * see {@link ArticleDocumentConverters}
     */
    @Test
    void savesAndReadsDataUsingCustomConverters() {
        String id = UUID.randomUUID().toString();
        ArticleDocument document = new ArticleDocument(id, "Anastasiia Smirnova", "The content of the article", true);
        repository.save(document);

        assertThat(repository.findById(id))
                .hasValue(document);
    }

    @Test
    void expiresDraftAccordingToConfiguration() throws InterruptedException {
        String id = UUID.randomUUID().toString();
        ArticleDocument document = new ArticleDocument(id, "Anastasiia Smirnova", "The content of the article", true);
        repository.save(document);

        assertThat(repository.findById(id))
                .hasValue(document);

        Thread.sleep(11_000);// expiration is set to 10 seconds; using naive approach to test expiration

        assertThat(repository.findById(id))
                .isEmpty();
    }
}
