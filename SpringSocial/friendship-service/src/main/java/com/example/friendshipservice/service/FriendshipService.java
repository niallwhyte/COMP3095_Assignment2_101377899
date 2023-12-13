package com.example.friendshipservice.service;

import com.example.friendshipservice.dto.FriendshipRequest;
import com.example.friendshipservice.dto.FriendshipResponse;

import java.util.List;

public interface FriendshipService {

    void sendFriendRequest(FriendshipRequest request);

    void acceptFriendRequest(String friendshipId);

    void declineFriendRequest(String friendshipId);

    FriendshipResponse getFriendshipStatus(String requesterId, String requesteeId);

    List<FriendshipResponse> getFriendRequestsForUser(String userId);

    List<FriendshipResponse> getFriendsOfUser(String userId);

    void removeFriend(String friendshipId);


}
