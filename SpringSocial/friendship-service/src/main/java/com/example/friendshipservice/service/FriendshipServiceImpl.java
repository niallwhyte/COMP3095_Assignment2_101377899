package com.example.friendshipservice.service;

import com.example.friendshipservice.dto.FriendshipRequest;
import com.example.friendshipservice.dto.FriendshipResponse;
import com.example.friendshipservice.dto.UserResponse;
import com.example.friendshipservice.model.Friendship;
import com.example.friendshipservice.repository.FriendshipRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final WebClient webClient;

    private static final String USER_SERVICE_URL = "http://user-service:8088/api/user/";

    public FriendshipServiceImpl(FriendshipRepository friendshipRepository, WebClient.Builder webClientBuilder) {
        this.friendshipRepository = friendshipRepository;
        this.webClient = webClientBuilder.baseUrl(USER_SERVICE_URL).build();
    }

    private UserResponse getUserById(String userId) {
        try {
            return webClient.get()
                    .uri("/{userId}", userId)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block(); // Synchronous call
        } catch (Exception e) {
            log.error("Error fetching user with id {}", userId, e);
            return null;
        }
    }

    @Override
    public void sendFriendRequest(FriendshipRequest request) {
        UserResponse requesterUser = getUserById(request.getRequesterId());
        UserResponse requesteeUser = getUserById(request.getRequesteeId());

        if (requesterUser == null || requesteeUser == null) {
            log.error("One or both users not found for friend request. Requester ID: {}, Requestee ID: {}", request.getRequesterId(), request.getRequesteeId());
        }

        Optional<Friendship> existingFriendship = friendshipRepository.findByRequesterIdAndRequesteeId(requesterUser.getId(), requesteeUser.getId());
        if (existingFriendship != null) {
            log.error("Friend request already exists between users {} and {}", requesterUser.getId(), requesteeUser.getId());
        }

        Friendship friendship = Friendship.builder()
                .requesterId(requesterUser.getId())
                .requesteeId(requesteeUser.getId())
                .status(Friendship.FriendshipStatus.PENDING)
                .build();
        friendshipRepository.save(friendship);

        log.info("Friend request sent from user {} to user {}", requesterUser.getId(), requesteeUser.getId());
    }


    @Override
    public void acceptFriendRequest(String friendshipId) {
        friendshipRepository.findById(friendshipId).ifPresent(friendship -> {
            friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
            friendshipRepository.save(friendship);
        });
    }

    @Override
    public void declineFriendRequest(String friendshipId) {
        friendshipRepository.findById(friendshipId).ifPresent(friendship -> {
            friendship.setStatus(Friendship.FriendshipStatus.DECLINED);
            friendshipRepository.save(friendship);
        });
    }

    @Override
    public FriendshipResponse getFriendshipStatus(String requesterId, String requesteeId) {
        return friendshipRepository.findByRequesterIdAndRequesteeId(requesterId, requesteeId)
                .map(friendship -> new FriendshipResponse(
                        friendship.getId(),
                        friendship.getRequesterId(),
                        friendship.getRequesteeId(),
                        FriendshipResponse.ResponseStatus.valueOf(friendship.getStatus().name())
                )).orElse(null);
    }

    @Override
    public List<FriendshipResponse> getFriendRequestsForUser(String userId) {
        return friendshipRepository.findByRequesteeIdAndStatus(userId, Friendship.FriendshipStatus.PENDING)
                .stream()
                .map(friendship -> new FriendshipResponse(
                        friendship.getId(),
                        friendship.getRequesterId(),
                        friendship.getRequesteeId(),
                        FriendshipResponse.ResponseStatus.valueOf(friendship.getStatus().name())
                )).collect(Collectors.toList());
    }

    @Override
    public List<FriendshipResponse> getFriendsOfUser(String userId) {
        return friendshipRepository.findByRequesteeIdOrRequesterIdAndStatus(userId, Friendship.FriendshipStatus.ACCEPTED)
                .stream()
                .map(friendship -> new FriendshipResponse(
                        friendship.getId(),
                        friendship.getRequesterId(),
                        friendship.getRequesteeId(),
                        FriendshipResponse.ResponseStatus.valueOf(friendship.getStatus().name())
                )).collect(Collectors.toList());
    }

    @Override
    public void removeFriend(String friendshipId) {
        friendshipRepository.deleteById(friendshipId);
    }
}

