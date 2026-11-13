package pt.rumos.mymovies.exception;

public class MovieAlreadyInWatchlistException extends RuntimeException {

    public MovieAlreadyInWatchlistException(final Long movieId) {
        super("Movie with ID %s already in watchlist".formatted(movieId));
    }
}
