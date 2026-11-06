package pt.rumos.mymovies.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovie(
        long id,
        String title,
        @JsonProperty("release_date") String releaseDate,
        String overview,
        @JsonProperty("vote_average") Double voteAverage
) {

}
