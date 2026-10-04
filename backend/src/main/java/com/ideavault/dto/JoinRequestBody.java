package com.ideavault.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JoinRequestBody(
        @NotNull Long requesterId,
        @Size(max = 300) String message) {
}