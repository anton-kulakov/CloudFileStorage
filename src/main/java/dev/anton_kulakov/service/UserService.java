package dev.anton_kulakov.service;

import dev.anton_kulakov.dto.SignUpDto;
import dev.anton_kulakov.dto.UserMapper;
import dev.anton_kulakov.exception.UsernameAlreadyTakenException;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User createUser(SignUpDto signUpDto) {
        String username = signUpDto.getUsername();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyTakenException("User with username %s is already exists".formatted(username));
        }

        User user = userMapper.toUser(signUpDto);
        return userRepository.save(user);
    }
}
