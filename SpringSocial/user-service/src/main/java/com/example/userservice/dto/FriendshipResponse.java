package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendshipResponse {

    private String id;
    private String requesterId;
    private String requesteeId;
    private ResponseStatus status;

    public enum ResponseStatus {
        PENDING, ACCEPTED, DECLINED
    }

}
