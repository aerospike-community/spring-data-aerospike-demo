package com.example.demo.persistence.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserDocument, Long> {
}
