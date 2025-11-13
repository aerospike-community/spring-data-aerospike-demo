package com.demo.simplecrudrest.service;

import com.demo.simplecrudrest.entity.User;
import com.demo.simplecrudrest.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    UserRepository aerospikeUserRepository;

    public Optional<User> findUserById(int id) {
        log.info("Looking for a user with id {}", id);
        return aerospikeUserRepository.findById(id);
    }

    public void addUser(User user) {
        log.info("Saving user with id {}", user.getId());
        aerospikeUserRepository.save(user);
    }

    public void removeUserById(int id) {
        log.info("Deleting user with id {}", id);
        aerospikeUserRepository.deleteById(id);
    }

    public List<User> findAllByIds(Iterable<Integer> ids) {
        return StreamSupport.stream(aerospikeUserRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());
    }
}
