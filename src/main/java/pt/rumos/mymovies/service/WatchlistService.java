package pt.rumos.mymovies.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.exception.MovieAlreadyInWatchlistException;
import pt.rumos.mymovies.exception.MovieNotFoundException;
import pt.rumos.mymovies.exception.UserNotFoundException;
import pt.rumos.mymovies.repository.MovieRepository;
import pt.rumos.mymovies.repository.UserRepository;

import java.util.HashSet;
import java.util.List;

@Service
public class WatchlistService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    
    public WatchlistService(final MovieRepository movieRepository, final UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public List<MovieResponse> getWatchlistForUser(final String userEmail) {
        final var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException(userEmail));

        return user.getWatchlist().stream().map(MovieResponse::fromDomain).toList();
    }

    @Transactional
    public List<MovieResponse> addMovieToWatchlist(final Long movieId, final String userEmail) {
        final var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException(userEmail));
        final var movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        final var watchlist = new HashSet<>(user.getWatchlist());
        final var result = watchlist.add(movie);
        
        if (result) {
            user.setWatchlist(watchlist);
        } else {
            throw new MovieAlreadyInWatchlistException(movieId);
        }
        
        return watchlist.stream().map(MovieResponse::fromDomain).toList();
    }

    @Transactional
    public void removeMovieFromWatchlist(final Long movieId, final String userEmail) {
        final var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException(userEmail));
        final var watchlist = new HashSet<>(user.getWatchlist());
        
        if (!watchlist.removeIf(movie -> movie.getId().equals(movieId))) {
            throw new MovieNotFoundException(movieId);
        }

        user.setWatchlist(watchlist);
    }
}
