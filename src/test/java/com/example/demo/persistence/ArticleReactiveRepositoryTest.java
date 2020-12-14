package com.example.demo.persistence;

import com.example.demo.DemoApplicationTests;
import com.example.demo.persistence.configuration.AerospikeReactiveConfiguration;
import com.example.demo.persistence.customconverters.ArticleDocument;
import com.example.demo.persistence.customconverters.ArticleDocumentConverters;
import com.example.demo.persistence.customconverters.ArticleReactiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {AerospikeReactiveConfiguration.class})
public class ArticleReactiveRepositoryTest extends DemoApplicationTests {

    @Autowired
    ArticleReactiveRepository repository;

    /**
     * see {@link ArticleDocumentConverters}
     */
    @Test
    void savesAndReadsDataUsingCustomConverters() {
        String id = UUID.randomUUID().toString();
        ArticleDocument document = new ArticleDocument(id, "Anastasiia Smirnova", "The content of the article", true);
        repository.save(document).block();

        assertThat(repository.findById(id).blockOptional())
                .hasValue(document);
    }

    @Test
    void expiresDraftAccordingToConfiguration() throws InterruptedException {
        String id = UUID.randomUUID().toString();
        ArticleDocument document = new ArticleDocument(id, "Anastasiia Smirnova", "The content of the article", true);
        repository.save(document).block();

        assertThat(repository.findById(id).blockOptional())
                .hasValue(document);

        Thread.sleep(11_000);// expiration is set to 10 seconds; using naive approach to test expiration

        assertThat(repository.findById(id).blockOptional())
                .isEmpty();
    }
}
