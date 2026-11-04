package pt.rumos.mymovies.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.rumos.mymovies.dto.MovieRequest;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.dto.RatingUpdateRequest;
import pt.rumos.mymovies.model.Movie;
import pt.rumos.mymovies.repository.MovieRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> findAll() {
        return repository.findAll().stream()
                .map(MovieResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<MovieResponse> findById(final Long id) {
        return repository.findById(id).map(MovieResponse::fromDomain);
    }

    public List<MovieResponse> findByTitleContainingIgnoreCase(final String title) {
        return repository.findByTitleContainingIgnoreCase(title).stream()
                .map(MovieResponse::fromDomain)
                .toList();
    }

    @Transactional
    public MovieResponse create(final MovieRequest request) {
        final var movie = new Movie(request.title(), request.director(), request.year());
        return MovieResponse.fromDomain(repository.save(movie));
    }

    @Transactional
    public Optional<MovieResponse> update(final Long id, final MovieRequest request) {
        return repository.findById(id).map(movie -> {
            movie.setTitle(request.title());
            movie.setDirector(request.director());
            movie.setYear(request.year());
            return MovieResponse.fromDomain(repository.save(movie));
        });
    }

    @Transactional
    public Optional<MovieResponse> updateRating(final Long id, final RatingUpdateRequest request) {
        return repository.findById(id).map(movie -> {
            movie.setRating(request.rating());
            return MovieResponse.fromDomain(repository.save(movie));
        });
    }

    @Transactional
    public boolean delete(final Long id) {
        return repository.findById(id).map(movie -> {
            repository.delete(movie);
            return true;
        }).orElse(false);
    }
}
