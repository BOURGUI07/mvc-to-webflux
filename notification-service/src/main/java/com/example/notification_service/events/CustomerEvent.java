package com.example.notification_service.events;

import lombok.Builder;

public sealed interface CustomerEvent {

    Long customerId();
    String email();
    String username();

    @Builder
    record Created(Long customerId,
                   String email,
                   String username) implements CustomerEvent {}


}
