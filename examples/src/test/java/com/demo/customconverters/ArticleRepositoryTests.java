package com.demo.customconverters;

import com.demo.customconverters.converter.ArticleDocumentConverters;
import com.demo.customconverters.entity.ArticleDocument;
import com.demo.customconverters.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleRepositoryTests extends CustomConvertersAerospikeDemoApplicationTest {

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
        assertThat(repository.findById(id)).hasValue(document);
    }

    @Test
    void expiresDraftAccordingToConfiguration() throws InterruptedException {
        String id = UUID.randomUUID().toString();
        ArticleDocument document = new ArticleDocument(id, "Anastasiia Smirnova", "The content of the article", true);

        repository.save(document);
        assertThat(repository.findById(id)).hasValue(document);

        Thread.sleep(11_000);
        // expiration is set to 10 seconds in ArticleDocumentToAerospikeWriteDataConverter;
        // using naive approach to test expiration
        assertThat(repository.findById(id)).isEmpty();
    }
}
