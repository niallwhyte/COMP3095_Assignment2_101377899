package com.example.friendshipservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "friendships")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String requesterId;

    @Column(nullable = false)
    private String requesteeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    public enum FriendshipStatus {
        PENDING, ACCEPTED, DECLINED
    }
}
