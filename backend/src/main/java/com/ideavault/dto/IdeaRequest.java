package com.ideavault.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record IdeaRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank String description,
        @Min(1) Integer teamSize,
        @NotNull Long ownerId,
        List<Long> skillIds) {
}