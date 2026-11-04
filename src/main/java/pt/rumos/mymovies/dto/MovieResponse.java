package pt.rumos.mymovies.dto;

import pt.rumos.mymovies.model.Movie;
import pt.rumos.mymovies.model.MovieDocument;

public record MovieResponse(
        String id,
        String title,
        String director,
        Integer year,
        Double rating
) {

    /**
     * Mapeia um objeto de domínio (Movie) para o DTO de resposta da API (MovieResponse).
     *
     * @param movie a entidade/record de domínio
     * @return uma nova instância de MovieResponse pronta para serialização JSON
     **/
    public static MovieResponse fromDomain(Movie movie) {
        if (movie == null) {
            return null;
        }
        return new MovieResponse(movie.id().toString(), movie.title(), movie.director(),
                                        movie.year(), movie.rating());
    }

    public static MovieResponse fromDomain(MovieDocument movieDocument) {
        if (movieDocument == null) {
            return null;
        }
        return new MovieResponse(movieDocument.getId(), movieDocument.getTitle(), movieDocument.getDirector(),
                movieDocument.getYear(), movieDocument.getRating());
    }

}
