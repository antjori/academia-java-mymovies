package pt.rumos.mymovies.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RatingUpdateRequest(
        @NotNull(message = "Rating must be provided")
        @Positive(message = "Rating must be a positive number")
        @DecimalMax(value = "10.0", message = "Rating must be a number between 0 and 10")
        Double rating) {
}
