package com.example.demo.simplecrudwithrestapi.service;

import com.example.demo.simplecrudwithrestapi.entity.User;
import com.example.demo.simplecrudwithrestapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class UserService {

    UserRepository aerospikeUserRepository;

    public Optional<User> findUserById(int id) {
        return aerospikeUserRepository.findById(id);
    }

    public void addUser(User user) {
        aerospikeUserRepository.save(user);
    }

    public void removeUserById(int id) {
        aerospikeUserRepository.deleteById(id);
    }

    public List<User> findAllByIds(Iterable<Integer> ids) {
        return StreamSupport.stream(aerospikeUserRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());
    }
}
