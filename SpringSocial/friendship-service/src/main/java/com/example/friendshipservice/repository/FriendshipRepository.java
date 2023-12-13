package com.example.friendshipservice.repository;

import com.example.friendshipservice.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {

    Optional<Friendship> findByRequesterIdAndRequesteeId(String requesterId, String requesteeId);

    @Query("SELECT f FROM Friendship f WHERE (f.requesteeId = :userId OR f.requesterId = :userId) AND f.status = :status")
    List<Friendship> findByRequesteeIdOrRequesterIdAndStatus(String userId, Friendship.FriendshipStatus status);

    List<Friendship> findByRequesteeIdAndStatus(String requesteeId, Friendship.FriendshipStatus status);

}
