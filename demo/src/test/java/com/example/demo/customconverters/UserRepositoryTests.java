package com.example.demo.customconverters;

import com.example.demo.customconverters.converter.UserDataConverters;
import com.example.demo.customconverters.entity.UserDocument;
import com.example.demo.customconverters.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTests extends CustomConvertersAerospikeDemoApplicationTest {

    @Autowired
    UserRepository repository;

    /**
     * see {@link UserDataConverters}
     */
    @Test
    void savesAndReadsUsingCustomConverter() {
        long id = 777L;
        UserDocument document = new UserDocument(id, new UserDocument.UserData("221B Baker Street, London", "uk"));

        repository.save(document);
        assertThat(repository.findById(id))
                .hasValue(new UserDocument(id, new UserDocument.UserData("221B BAKER STREET, LONDON", "UK")));
    }
}