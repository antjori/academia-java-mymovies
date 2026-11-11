package pt.rumos.mymovies.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.exception.MovieNotFoundException;
import pt.rumos.mymovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WatchlistControllerTest {

    private static final String BASE_URL = "/api/movies";

    private static final String WATCHLIST_URL = BASE_URL + "/watchlist";

    private MockMvc mockMvc;

    @Mock
    private WatchlistService watchlistService;

    @InjectMocks
    private WatchlistController watchlistController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(watchlistController).build();
    }

    // ----------------------------------
    // Get Watchlist (GET /api/watchlist)
    // ----------------------------------

    @Test
    void givenValidRequest_whenWatchlistIsEmpty_thenReturnEmptyList() throws Exception {
        // given
        when(watchlistService.getWatchlist()).thenReturn(new ArrayList<>());

        // when
        mockMvc.perform(get(WATCHLIST_URL))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(watchlistService).getWatchlist();
    }

    @Test
    void givenValidRequest_whenWatchlistIsNotEmpty_thenReturnListWithMovies() throws Exception {
        // given
        final var movie1 = MovieResponse.fromDomain(new Movie(1L, "The Godfather", "Francis Ford Coppola", 1972, 9_2d));
        final var movie2 = MovieResponse.fromDomain(new Movie(2L, "The Shawshank Redemption", "Frank Darabont", 1994,
                9_3d));

        when(watchlistService.getWatchlist()).thenReturn(List.of(movie1, movie2));

        // when
        mockMvc.perform(get(WATCHLIST_URL))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(watchlistService).getWatchlist();
    }

    // ------------------------------------------------------
    // Add movie to Watchlist (POST /api/watchlist/{movieId})
    // ------------------------------------------------------

    @Test
    void givenValidMovieId_whenMovieIsNotInWatchlist_thenAddMovieToWatchlist() throws Exception {
        // given
        final var movieId = 1L;

        when(watchlistService.addToWatchlist(movieId)).thenReturn(WatchlistResponse.fromDomain(new Movie(movieId,
                "The Godfather", "Francis Ford Coppola", 1972, 9.2d)));

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("The Godfather"))
                .andExpect(jsonPath("$.director").value("Francis Ford Coppola"))
                .andExpect(jsonPath("$.year").value(1972))
                .andExpect(jsonPath("$.rating").value(9.2));

        verify(watchlistService).addToWatchlist(movieId);
    }

    @Test
    void givenValidMovieId_whenMovieIsAlreadyInWatchlist_thenReturnConflict() throws Exception {
        // given
        final var movieId = 1L;

        when(watchlistService.addToWatchlist(movieId)).thenThrow(new MovieAlreadyInWatchlistException(1L));

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("MOVIE_IN_WATCHLIST"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Movie with ID 1 already in watchlist"));

        verify(watchlistService).addToWatchlist(movieId);
    }

    @Test
    void givenUnknownMovieId_whenAddingMovieToWatchlist_thenReturnNotFound() throws Exception {
        // given
        final var movieId = 99L;

        when(watchlistService.addToWatchlist(movieId)).thenThrow(new MovieNotFoundException(movieId));

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("MOVIE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Movie with ID 99 not found"));

        verify(watchlistService).addToWatchlist(movieId);
    }

    // -------------------------------------------------------------
    // Remove movie from Watchlist (DELETE /api/watchlist/{movieId})
    // -------------------------------------------------------------

    @Test
    void givenValidMovieId_whenMovieIsInWatchlist_thenMovieIsRemoved() throws Exception {
        // given
        final var movieId = 1L;

        when(watchlistService.removeFromWatchlist(movieId)).thenReturn(true);

        // when
        mockMvc.perform(delete(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(watchlistService).removeFromWatchlist(movieId);
    }

    @Test
    void givenValidMovieId_whenMovieIsNotInWatchList_thenReturnNotFound() throws Exception {
        // given
        final var movieId = 99L;

        when(watchlistService.removeFromWatchlist(movieId)).thenReturn(false);

        // when
        mockMvc.perform(delete(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("MOVIE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Movie with ID 99 not found"));

        verify(watchlistService).removeFromWatchlist(movieId);
    }
}
