package com.example.userservice.controller;

import com.example.userservice.dto.FriendshipResponse;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserRequest userRequest){
        userService.createUser(userRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(){ return userService.getAllUsers();}

    @GetMapping("/friends/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<FriendshipResponse>> getUserFriends(@PathVariable String userId) {

        List<FriendshipResponse> friends = userService.getUserFriends(userId);

        return ResponseEntity.ok(friends);
    }

    @GetMapping("/requests/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<FriendshipResponse>> getUserFriendRequests(@PathVariable String userId) {

        List<FriendshipResponse> friendRequests = userService.getUserFriendRequests(userId);

        return ResponseEntity.ok(friendRequests);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") String userId, @RequestBody UserRequest userRequest){

        String updatedUserId = userService.updateUser(userId, userRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/user/" + updatedUserId);

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") String userId){

        userService.deleteUser(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {

        UserResponse userResponse = userService.getUserById(userId);

        if (userResponse == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

}
