package com.example.demo.simplecrudwithrestapi;

import com.example.demo.simplecrudwithrestapi.entity.User;
import com.example.demo.simplecrudwithrestapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTests extends SimpleCrudWithRestAPIAerospikeDemoApplicationTest {

    int id, age;
    User user;

    @Autowired
    UserRepository repository;

    @BeforeEach
    void setUp() {
        id = new Random().nextInt();
        age = new Random().nextInt(1, 50);
        user = new User(id, "Michael", "m@abc.com", age);
    }

    @Test
    public void saveUser() {
        repository.save(user);
        assertThat(repository.findById(id)).hasValue(user);
    }

    @Test
    public void deleteUser() {
        repository.delete(user);
        assertThat(repository.existsById(id)).isFalse();
    }
}
