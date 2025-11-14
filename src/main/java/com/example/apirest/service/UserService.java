package com.example.apirest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.apirest.dto.UserDTO;
import com.example.apirest.model.User;
import com.example.apirest.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = toEntity(userDTO);
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        User updated = userRepository.save(user);
        return toDTO(updated);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}