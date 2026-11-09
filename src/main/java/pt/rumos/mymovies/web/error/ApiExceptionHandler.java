package pt.rumos.mymovies.web.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import pt.rumos.mymovies.exception.MovieNotFoundException;

import java.time.Instant;

@RestControllerAdvice
public final class ApiExceptionHandler {

    private enum StatusCode {
        INTERNAL_ERROR,
        MOVIE_NOT_FOUND,
        VALIDATION_ERROR,
    }

    private static final String BASE_PATH = "/api/movies";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MovieNotFoundException.class)
    public ApiError handleMovieNotFoundException(final MovieNotFoundException ex) {
        return new ApiError(Instant.now(), HttpStatus.NOT_FOUND.value(), StatusCode.MOVIE_NOT_FOUND.name(), ex.getMessage(), 
                BASE_PATH + "/" + ex.getId());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException manvx,
                                                          final HttpServletRequest request) {
        return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), StatusCode.VALIDATION_ERROR.name(), manvx.getMessage(), 
                request.getContextPath());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleException(final Exception ex, final HttpServletRequest request) {
        LOGGER.error("event=API_UNEXPECTED path={} type={}", request.getRequestURI(),
                ex.getClass().getSimpleName(), ex);
        return new ApiError(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), StatusCode.INTERNAL_ERROR.name(), ex.getMessage(),
                request.getContextPath());
    }
}
