package com.example.userservice.service;

import com.example.userservice.dto.FriendshipResponse;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;

import java.util.List;
public interface UserService {

    void createUser(UserRequest userRequest);

    String updateUser(String userId, UserRequest userRequest);

    void deleteUser(String userId);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(String userId);

    List<FriendshipResponse> getUserFriends(String userId);

    List<FriendshipResponse> getUserFriendRequests(String userId);


}
