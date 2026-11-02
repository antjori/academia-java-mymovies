package pt.rumos.mymovies.dto;

import pt.rumos.mymovies.model.Movie;

public record MovieResponse(Long id, String title, String director, Integer year, Double rating) {

    public static MovieResponse fromDomain(final Movie movie) {
        return new MovieResponse(movie.id(), movie.title(), movie.director(), movie.year(), movie.rating());
    }
}
