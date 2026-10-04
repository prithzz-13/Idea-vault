package com.ideavault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SkillRequest(@NotBlank @Size(max = 50) String name) {}