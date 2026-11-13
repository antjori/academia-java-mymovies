package pt.rumos.mymovies.web.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pt.rumos.mymovies.exception.MovieAlreadyInWatchlistException;
import pt.rumos.mymovies.exception.MovieNotFoundException;
import pt.rumos.mymovies.exception.UserNotFoundException;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleMovieNotFound(MovieNotFoundException ex, HttpServletRequest request) {
        log.error("event=API_MOVIE_NOT_FOUND path={} type={}", request.getRequestURI(), ex.getClass().getSimpleName());
        return new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "MOVIE_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("event=API_BAD_REQUEST path={} type={}", request.getRequestURI(), ex.getClass().getSimpleName());
        return new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Falha na validação dos campos enviados.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        log.error("event=API_USER_NOT_FOUND path={} type={}", request.getRequestURI(), ex.getClass().getSimpleName());
        return new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "USER_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MovieAlreadyInWatchlistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleMovieAlreadyInWatchlist(MovieAlreadyInWatchlistException ex, HttpServletRequest request) {
        log.error("event=API_CONFLICT path={} type={}", request.getRequestURI(), ex.getClass().getSimpleName());
        return new ApiError(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "MOVIE_ALREADY_IN_WATCHLIST",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("event=API_UNEXPECTED path={} type={}", request.getRequestURI(), ex.getClass().getSimpleName(), ex);
        return new ApiError(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "Ocorreu um erro interno inesperado no servidor.",
                request.getRequestURI()
        );
    }
}
