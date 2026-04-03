package com.demo.multiplecustomconverters;

import com.demo.multiplecustomconverters.entity.Price;
import com.demo.multiplecustomconverters.entity.ProductDocument;
import com.demo.multiplecustomconverters.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductRepositoryTests extends MultipleCustomConvertersAerospikeDemoApplicationTest {

    @Autowired
    ProductRepository repository;

    @Test
    void savesAndReadsProductWithPriceConverter() {
        String id = UUID.randomUUID().toString();
        ProductDocument product = ProductDocument.builder()
            .id(id)
            .name("Wireless Headphones")
            .price(new Price(79.99, "USD"))
            .build();

        repository.save(product);
        assertThat(repository.findById(id)).hasValue(product);
    }

    @Test
    void savesAndReadsProductWithDifferentCurrency() {
        String id = UUID.randomUUID().toString();
        ProductDocument product = ProductDocument.builder()
            .id(id)
            .name("Mechanical Keyboard")
            .price(new Price(149.50, "EUR"))
            .build();

        repository.save(product);
        assertThat(repository.findById(id)).hasValue(product);

        repository.deleteById(id);
        assertThat(repository.findById(id)).isNotPresent();
    }
}
