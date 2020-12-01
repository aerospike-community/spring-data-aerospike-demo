package com.example.demo.persistence.customconverters;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserDocument, Long> {
}
