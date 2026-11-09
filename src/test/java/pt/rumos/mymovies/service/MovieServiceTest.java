package pt.rumos.mymovies.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.rumos.mymovies.exception.MovieNotFoundException;
import pt.rumos.mymovies.model.Movie;
import pt.rumos.mymovies.repository.MovieRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void findById_WhenMovieExists_ShouldReturnMovieResponse() {
        // given
        final var movie = new Movie();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // when
        final var movieResponse = movieService.findById(anyLong());

        // then
        assertNotNull(movieResponse);
    }

    @Test
    void findById_WhenMovieDoesNotExist_ShouldThrowMovieNotFoundException() {
        // given
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        final var exception = assertThrows(MovieNotFoundException.class, () -> movieService.findById(99L));

        // then
        assertNotNull(exception);
        assertEquals("Movie not found with id: 99", exception.getMessage());
    }
}
