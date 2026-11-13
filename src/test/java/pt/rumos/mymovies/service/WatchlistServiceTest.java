package pt.rumos.mymovies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.exception.MovieAlreadyInWatchlistException;
import pt.rumos.mymovies.exception.MovieNotFoundException;
import pt.rumos.mymovies.exception.UserNotFoundException;
import pt.rumos.mymovies.model.Movie;
import pt.rumos.mymovies.model.User;
import pt.rumos.mymovies.repository.MovieRepository;
import pt.rumos.mymovies.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    private static final String USER_EMAIL = "user@example.com";
    private static final Long MOVIE_ID = 1L;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    private WatchlistService watchlistService;

    @BeforeEach
    void setUp() {
        watchlistService = new WatchlistService(movieRepository, userRepository);
    }

    // -------------------
    // getWatchlistForUser
    // -------------------

    @Test
    void getWatchlistForUser_shouldReturnMovies_whenUserExists() {
        // given
        final var movie = createMovie(MOVIE_ID);
        final var user = createUser(Set.of(movie));

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        // when
        final var result = watchlistService.getWatchlistForUser(USER_EMAIL);

        // then
        assertEquals(1, result.size());
        assertEquals(MOVIE_ID, result.getFirst().id());

        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void getWatchlistForUser_shouldReturnEmptyList_whenWatchlistIsEmpty() {
        // given
        final var user = createUser(Set.of());

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        // when
        final var result = watchlistService.getWatchlistForUser(USER_EMAIL);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void getWatchlistForUser_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // given
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // when
        final var exception = assertThrows(UserNotFoundException.class,
                () -> watchlistService.getWatchlistForUser(USER_EMAIL));

        // then
        assertNotNull(exception);
        assertEquals("User with email %s not found".formatted(USER_EMAIL), exception.getMessage());

        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(movieRepository);
    }

    // -------------------
    // addMovieToWatchlist
    // -------------------

    @Test
    void addMovieToWatchlist_shouldAddMovie_whenUserAndMovieExist() {
        // given
        final var existingMovie = createMovie(2L);
        final var movieToAdd = createMovie(MOVIE_ID);

        final var user = createUser(Set.of(existingMovie));

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.of(movieToAdd));

        // when
        final var result = watchlistService.addMovieToWatchlist(MOVIE_ID, USER_EMAIL);

        // then
        assertEquals(2, result.size());
        assertTrue(user.getWatchlist().stream().anyMatch(movie -> movie.getId().equals(MOVIE_ID)));

        verify(userRepository).findByEmail(USER_EMAIL);
        verify(movieRepository).findById(MOVIE_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    void addMovieToWatchlist_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // given
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // when
        final var exception = assertThrows(UserNotFoundException.class,
                () -> watchlistService.addMovieToWatchlist(MOVIE_ID, USER_EMAIL));

        // then
        assertNotNull(exception);
        assertEquals("User with email %s not found".formatted(USER_EMAIL), exception.getMessage());

        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void addMovieToWatchlist_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        // given
        final var user = createUser(Set.of());

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.empty());

        // when
        final var exception = assertThrows(MovieNotFoundException.class,
                () -> watchlistService.addMovieToWatchlist(MOVIE_ID, USER_EMAIL));

        // then
        assertNotNull(exception);
        assertEquals("Movie not found: %d".formatted(MOVIE_ID), exception.getMessage());

        verify(userRepository).findByEmail(USER_EMAIL);
        verify(movieRepository).findById(MOVIE_ID);
    }

    @Test
    void addMovieToWatchlist_shouldThrowMovieAlreadyInWatchlistException_whenMovieAlreadyExists() {
        // given
        final var movie = createMovie(MOVIE_ID);
        final var user = createUser(Set.of(movie));

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.of(movie));

        // when
        final var exception = assertThrows(MovieAlreadyInWatchlistException.class,
                () -> watchlistService.addMovieToWatchlist(MOVIE_ID, USER_EMAIL));

        // then
        assertNotNull(exception);
        assertEquals("Movie with ID %d already in watchlist".formatted(MOVIE_ID), exception.getMessage());
        assertEquals(1, user.getWatchlist().size());

        verify(userRepository).findByEmail(USER_EMAIL);
        verify(movieRepository).findById(MOVIE_ID);
    }

    // ------------------------
    // removeMovieFromWatchlist
    // ------------------------

    @Test
    void removeMovieFromWatchlist_shouldRemoveMovie_whenMovieExists() {
        // given
        final var movieToRemove = createMovie(MOVIE_ID);
        final var remainingMovie = createMovie(2L);
        final var user = createUser(Set.of(movieToRemove, remainingMovie));

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        // when
        assertDoesNotThrow(() -> watchlistService.removeMovieFromWatchlist(MOVIE_ID, USER_EMAIL));

        // then
        assertEquals(1, user.getWatchlist().size());
        assertTrue(user.getWatchlist().stream().anyMatch(movie -> movie.getId().equals(2L)));
        assertFalse(user.getWatchlist().stream().anyMatch(movie -> movie.getId().equals(MOVIE_ID)));

        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void removeMovieFromWatchlist_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // given
        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.empty());

        // when
        final var exception = assertThrows(UserNotFoundException.class,
                () -> watchlistService.removeMovieFromWatchlist(MOVIE_ID, USER_EMAIL));

        // then
        assertNotNull(exception);
        assertEquals("User with email %s not found".formatted(USER_EMAIL), exception.getMessage());

        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(movieRepository);
    }

    @Test
    void removeMovieFromWatchlist_shouldThrowMovieNotFoundException_whenMovieIsNotInWatchlist() {
        // given
        final var user = createUser(Set.of(createMovie(2L)));

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        // when
        final var exception = assertThrows(MovieNotFoundException.class,
                () -> watchlistService.removeMovieFromWatchlist(MOVIE_ID, USER_EMAIL));

        // then
        assertNotNull(exception);
        assertEquals("Movie not found: %d".formatted(MOVIE_ID), exception.getMessage());
        assertEquals(1, user.getWatchlist().size());

        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(movieRepository);
    }

    private Movie createMovie(Long id) {
        final var movie = new Movie();
        movie.setId(id);

        return movie;
    }

    private User createUser(Set<Movie> watchlist) {
        final var user = new User();
        user.setEmail(USER_EMAIL);
        user.setWatchlist(new HashSet<>(watchlist));

        return user;
    }
}
