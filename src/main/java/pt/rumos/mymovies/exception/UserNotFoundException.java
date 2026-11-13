package pt.rumos.mymovies.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final String userEmail) {
        super("User with email %s not found".formatted(userEmail));
    }
}
