package pt.rumos.mymovies.exception;

public final class MovieNotFoundException extends RuntimeException {

    private final Long id;
    
    public MovieNotFoundException(final Long id) {
        super("Movie not found with id: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
