package pt.rumos.mymovies.dto;

import pt.rumos.mymovies.model.Movie;

public record MovieResponse(
        Long id,
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
        return new MovieResponse(movie.getId(), movie.getTitle(), movie.getDirector(),
                                        movie.getYear(), movie.getRating());
    }

}
