package com.demo.multiplecustomconverters;

import com.demo.multiplecustomconverters.entity.Address;
import com.demo.multiplecustomconverters.entity.StoreDocument;
import com.demo.multiplecustomconverters.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class StoreRepositoryTests extends MultipleCustomConvertersAerospikeDemoApplicationTest {

    @Autowired
    StoreRepository repository;

    @Test
    void savesAndReadsStoreWithAddressConverter() {
        String id = UUID.randomUUID().toString();
        StoreDocument store = StoreDocument.builder()
            .id(id)
            .name("Downtown Electronics")
            .location(new Address("123 Main Street", "Springfield", "US"))
            .build();

        repository.save(store);
        assertThat(repository.findById(id)).hasValue(store);
    }

    @Test
    void savesAndReadsStoreWithDifferentAddress() {
        String id = UUID.randomUUID().toString();
        StoreDocument store = StoreDocument.builder()
            .id(id)
            .name("Berlin Tech Store")
            .location(new Address("Alexanderplatz 1", "Berlin", "DE"))
            .build();

        repository.save(store);
        assertThat(repository.findById(id)).hasValue(store);

        repository.deleteById(id);
        assertThat(repository.findById(id)).isNotPresent();
    }
}
