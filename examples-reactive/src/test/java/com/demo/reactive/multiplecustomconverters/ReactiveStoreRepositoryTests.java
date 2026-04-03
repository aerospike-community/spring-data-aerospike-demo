package com.demo.reactive.multiplecustomconverters;

import com.demo.reactive.multiplecustomconverters.entity.Address;
import com.demo.reactive.multiplecustomconverters.entity.StoreDocument;
import com.demo.reactive.multiplecustomconverters.repository.ReactiveStoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.UUID;

public class ReactiveStoreRepositoryTests extends ReactiveMultipleCustomConvertersAerospikeDemoApplicationTest {

    @Autowired
    ReactiveStoreRepository repository;

    @Test
    void savesAndReadsStoreWithAddressConverter() {
        String id = UUID.randomUUID().toString();
        StoreDocument store = StoreDocument.builder()
            .id(id)
            .name("Downtown Electronics")
            .location(new Address("123 Main Street", "Springfield", "US"))
            .build();

        StepVerifier.create(
            repository.save(store)
                .then(repository.findById(id))
        ).expectNext(store).verifyComplete();
    }

    @Test
    void savesAndReadsStoreWithDifferentAddress() {
        String id = UUID.randomUUID().toString();
        StoreDocument store = StoreDocument.builder()
            .id(id)
            .name("Berlin Tech Store")
            .location(new Address("Alexanderplatz 1", "Berlin", "DE"))
            .build();

        StepVerifier.create(
            repository.save(store)
                .then(repository.findById(id))
        ).expectNext(store).verifyComplete();

        StepVerifier.create(
            repository.deleteById(id)
                .then(repository.findById(id))
        ).verifyComplete();
    }
}
