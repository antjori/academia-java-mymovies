package pt.rumos.mymovies.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pt.rumos.mymovies.dto.MovieRequest;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.dto.RatingUpdateRequest;
import pt.rumos.mymovies.model.MovieDocument;
import pt.rumos.mymovies.repository.MovieDocumentRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieDocumentRepository movieDocumentRepository;

    public MovieService(MovieDocumentRepository movieDocumentRepository) {
        this.movieDocumentRepository = movieDocumentRepository;
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> findAll() {
        return movieDocumentRepository.findAll().stream()
                .map(MovieResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<MovieResponse> findById(final String id) {
        return movieDocumentRepository.findById(id).map(MovieResponse::fromDomain);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> findByTitleContainingIgnoreCase(final String title) {
        return movieDocumentRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(MovieResponse::fromDomain)
                .toList();
    }

    @Transactional
    public MovieResponse create(final MovieRequest request) {
        final var movieDocument = new MovieDocument();
        movieDocument.setTitle(request.title());
        movieDocument.setDirector(request.director());
        movieDocument.setYear(request.year());

        return MovieResponse.fromDomain(movieDocumentRepository.save(movieDocument));
    }

    @Transactional
    public Optional<MovieResponse> update(final String id, final MovieRequest request) {
        return movieDocumentRepository.findById(id).map(movieDocument -> {
            movieDocument.setTitle(request.title());
            movieDocument.setDirector(request.director());
            movieDocument.setYear(request.year());
            return movieDocumentRepository.save(movieDocument);
        }).map(MovieResponse::fromDomain);
    }

    @Transactional
    public Optional<MovieResponse> updateRating(final String id, final RatingUpdateRequest request) {
        return movieDocumentRepository.findById(id).map(movieDocument -> {
            movieDocument.setRating(request.rating());
            return movieDocumentRepository.save(movieDocument);
        }).map(MovieResponse::fromDomain);
    }

    @Transactional
    public boolean delete(final String id) {
        return movieDocumentRepository.findById(id).map(movieDocument -> {
            movieDocumentRepository.delete(movieDocument);
            return true;
        }).orElse(false);
    }
}
