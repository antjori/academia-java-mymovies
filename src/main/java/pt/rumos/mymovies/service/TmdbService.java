package pt.rumos.mymovies.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.rumos.mymovies.client.TmdbClient;
import pt.rumos.mymovies.dto.MovieCandidateResponse;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.dto.tmdb.TmdbMovie;
import pt.rumos.mymovies.dto.tmdb.TmdbSearchResponse;
import pt.rumos.mymovies.model.Movie;
import pt.rumos.mymovies.repository.MovieRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class TmdbService {

    private final TmdbClient tmdbClient;

    private final MovieRepository movieRepository;

    public TmdbService(final TmdbClient tmdbClient, final MovieRepository movieRepository) {
        this.tmdbClient = tmdbClient;
        this.movieRepository = movieRepository;
    }

    public List<MovieCandidateResponse> searchCandidates(final String query) {
        TmdbSearchResponse response = tmdbClient.searchMovies(query);
        if (response == null || response.results() == null) {
            return List.of();
        }
        return response.results().stream()
                .map(tmdb -> new MovieCandidateResponse(
                        tmdb.id(),
                        tmdb.title(),
                        tmdb.releaseDate()))
                .toList();
    }

    @Transactional
    public MovieResponse importMovie(final long tmdbId) {
        TmdbMovie external = tmdbClient.getMovieDetails(tmdbId);
        int year = 2000;
        if (external.releaseDate() != null && !external.releaseDate().isBlank()) {
            year = LocalDate.parse(external.releaseDate()).getYear();
        }
        Movie movie = new Movie();
        movie.setTitle(external.title());
        movie.setDirector("Desconhecido (TMDB)");
        movie.setYear(year);
        movie.setRating(external.voteAverage() != null ? external.voteAverage() : 0.0);
        Movie saved = movieRepository.save(movie);
        return new MovieResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDirector(),
                saved.getYear(),
                saved.getRating()
        );
    }
}
