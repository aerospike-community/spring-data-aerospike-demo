package com.demo.reactive.customconverters;

import com.demo.reactive.customconverters.converter.UserDataConverters;
import com.demo.reactive.customconverters.entity.UserDocument;
import com.demo.reactive.customconverters.repository.ReactiveUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTests extends ReactiveCustomConvertersAerospikeDemoApplicationTest {

    @Autowired
    ReactiveUserRepository repository;

    /**
     * see {@link UserDataConverters}
     */
    @Test
    void savesAndReadsUsingCustomConverter() {
        long id = 777L;
        UserDocument document = new UserDocument(id, new UserDocument.UserData("221B Baker Street, London", "uk"));

        StepVerifier.create(repository.save(document)
                        .then(repository.findById(id)))
                .expectNextMatches(result -> {
                    assertThat(result).isEqualTo(
                            new UserDocument(id, new UserDocument.UserData("221B BAKER STREET, LONDON", "UK"))
                    );
                    return true;
                })
                .verifyComplete();
    }
}
