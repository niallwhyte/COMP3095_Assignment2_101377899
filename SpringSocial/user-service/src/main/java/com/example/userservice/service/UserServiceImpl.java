package com.example.userservice.service;

import com.example.userservice.dto.FriendshipResponse;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WebClient webClient;

    private static final String FRIENDSHIP_SERVICE_URL = "http://friendship-service:8084/api/friendships/";

    @Autowired
    public UserServiceImpl(UserRepository userRepository, WebClient.Builder webClientBuilder) {
        this.userRepository = userRepository;
        this.webClient = webClientBuilder.baseUrl(FRIENDSHIP_SERVICE_URL).build();
    }

    @Override
    public void createUser(UserRequest userRequest){

        log.info("Creating a new user {}", userRequest.getName());

        User user = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .build();

        userRepository.save(user);

        log.info("User {} is saved", user.getId());
    }

    @Override
    public String updateUser(String userId, UserRequest userRequest){

        log.info("Updating a user with id {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()){
            User user = userOptional.get();
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());

            log.info("User {} is updated", user.getId());
            userRepository.save(user);
            return user.getId();
        }

        return userId;
    }

    @Override
    public void deleteUser(String userId){

        log.info("User {} is deleted", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse getUserById(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            return null;
        }

        User user = userOptional.get();
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());

        return userResponse;
    }

    @Override
    public List<UserResponse> getAllUsers(){

        log.info("Return a list of users");

        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserResponse).toList();
    }

    public List<FriendshipResponse> getUserFriends(String userId) {
        return webClient.get()
                .uri("/friends/" + userId)
                .retrieve()
                .bodyToFlux(FriendshipResponse.class)
                .collectList()
                .block();
    }

    public List<FriendshipResponse> getUserFriendRequests(String userId) {
        return webClient.get()
                .uri("/requests/" + userId)
                .retrieve()
                .bodyToFlux(FriendshipResponse.class)
                .collectList()
                .block();
    }

    private UserResponse mapToUserResponse(User user){

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
