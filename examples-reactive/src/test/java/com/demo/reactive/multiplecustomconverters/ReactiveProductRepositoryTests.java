package com.demo.reactive.multiplecustomconverters;

import com.demo.reactive.multiplecustomconverters.entity.Price;
import com.demo.reactive.multiplecustomconverters.entity.ProductDocument;
import com.demo.reactive.multiplecustomconverters.repository.ReactiveProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.UUID;

public class ReactiveProductRepositoryTests extends ReactiveMultipleCustomConvertersAerospikeDemoApplicationTest {

    @Autowired
    ReactiveProductRepository repository;

    @Test
    void savesAndReadsProductWithPriceConverter() {
        String id = UUID.randomUUID().toString();
        ProductDocument product = ProductDocument.builder()
            .id(id)
            .name("Wireless Headphones")
            .price(new Price(79.99, "USD"))
            .build();

        StepVerifier.create(
            repository.save(product)
                .then(repository.findById(id))
        ).expectNext(product).verifyComplete();
    }

    @Test
    void savesAndReadsProductWithDifferentCurrency() {
        String id = UUID.randomUUID().toString();
        ProductDocument product = ProductDocument.builder()
            .id(id)
            .name("Mechanical Keyboard")
            .price(new Price(149.50, "EUR"))
            .build();

        StepVerifier.create(
            repository.save(product)
                .then(repository.findById(id))
        ).expectNext(product).verifyComplete();

        StepVerifier.create(
            repository.deleteById(id)
                .then(repository.findById(id))
        ).verifyComplete();
    }
}
