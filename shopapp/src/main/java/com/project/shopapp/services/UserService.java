package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User createUser(UserDTO userDTO) {
        boolean existPhoneNumber = userRepository.existsByPhoneNumber(userDTO.getPhoneNumber());
        if (existPhoneNumber) {
            throw new DataIntegrityViolationException("phone number is already register");
        }

        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();

        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            newUser.setPassword(userDTO.getPassword());
        }
        Role role = null;
        try {
            role = roleRepository.findById(userDTO.getRoleId()).orElseThrow(() -> new DataNotFoundException("not found role"));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }
        newUser.setRole(role);

        return null;
    }

    @Override
    public String login(UserDTO userDTO) {
        return null;
    }
}
