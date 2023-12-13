package com.example.friendshipservice.controller;

import com.example.friendshipservice.dto.FriendshipRequest;
import com.example.friendshipservice.dto.FriendshipResponse;
import com.example.friendshipservice.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @PostMapping("/request")
    public ResponseEntity<Void> sendFriendRequest(@RequestBody FriendshipRequest request) {

        friendshipService.sendFriendRequest(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable String id) {

        friendshipService.acceptFriendRequest(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/decline/{id}")
    public ResponseEntity<Void> declineFriendRequest(@PathVariable String id) {

        friendshipService.declineFriendRequest(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status/{requesterId}/{requesteeId}")
    public ResponseEntity<FriendshipResponse> getFriendshipStatus(@PathVariable String requesterId, @PathVariable String requesteeId) {

        FriendshipResponse response = friendshipService.getFriendshipStatus(requesterId, requesteeId);

        if(response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requests/{userId}")
    public ResponseEntity<List<FriendshipResponse>> getFriendRequestsForUser(@PathVariable String userId) {

        List<FriendshipResponse> responses = friendshipService.getFriendRequestsForUser(userId);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/friends/{userId}")
    public ResponseEntity<List<FriendshipResponse>> getFriendsOfUser(@PathVariable String userId) {

        List<FriendshipResponse> responses = friendshipService.getFriendsOfUser(userId);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFriend(@PathVariable String id) {

        friendshipService.removeFriend(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
