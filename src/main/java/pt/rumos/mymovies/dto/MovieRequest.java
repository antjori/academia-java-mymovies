package pt.rumos.mymovies.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MovieRequest(
        @NotBlank(message = "Title cannot be null or empty")
        @Size(max = 120, message = "Title must have maximum 120 characters")
        String title,
        @NotBlank(message = "Director cannot be null or empty")
        String director,
        @NotNull(message = "Year cannot be null")
        @Min(value = 1888, message = "Year must be greater than 1888")
        Integer year,
        Double rating
) {}
