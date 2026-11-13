package pt.rumos.mymovies.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import pt.rumos.mymovies.service.WatchlistService;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/scenario/user.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS, scripts = "/scenario/user_cleanup.sql")
class WatchlistControllerTest {

    private static final String BASE_URL = "/api/movies";

    private static final String WATCHLIST_URL = BASE_URL + "/watchlist";

    private static final String USER_EMAIL = "aluno@rumos.pt";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WatchlistService watchlistService;

    // ----------------------------------
    // Get Watchlist (GET /api/watchlist)
    // ----------------------------------

    @Test
    void givenValidRequest_whenWatchlistIsEmpty_thenReturnEmptyList() throws Exception {
        // given

        // when
        mockMvc.perform(get(WATCHLIST_URL).with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/scenario/watchlist.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/scenario/watchlist_cleanup.sql")
    void givenValidRequest_whenWatchlistIsNotEmpty_thenReturnListWithMovies() throws Exception {
        // given

        // when
        mockMvc.perform(get(WATCHLIST_URL).with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ------------------------------------------------------
    // Add movie to Watchlist (POST /api/watchlist/{movieId})
    // ------------------------------------------------------

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/scenario/watchlist_cleanup.sql")
    void givenValidMovieId_whenMovieIsNotInWatchlist_thenAddMovieToWatchlist() throws Exception {
        // given
        final var movieId = 3L;

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(movieId))
                .andExpect(jsonPath("$.[0].title").value("Interstellar"))
                .andExpect(jsonPath("$.[0].director").value("Christopher Nolan"))
                .andExpect(jsonPath("$.[0].year").value(2014))
                .andExpect(jsonPath("$.[0].rating").isEmpty());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/scenario/watchlist.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/scenario/watchlist_cleanup.sql")
    void givenValidMovieId_whenMovieIsAlreadyInWatchlist_thenReturnConflict() throws Exception {
        // given
        final var movieId = 1L;

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("MOVIE_IN_WATCHLIST"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Movie with ID 1 already in watchlist"));
    }

    @Test
    void givenUnknownMovieId_whenAddingMovieToWatchlist_thenReturnMovieNotFound() throws Exception {
        // given
        final var movieId = 99L;

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("MOVIE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Movie not found: %d".formatted(movieId)));
    }

    @Test
    void givenUnknownUserEmail_whenAddingMovieToWatchlist_thenReturnUserNotFound() throws Exception {
        // given
        final var movieId = 99L;

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with email %s not found".formatted(USER_EMAIL)));
    }

    // -------------------------------------------------------------
    // Remove movie from Watchlist (DELETE /api/watchlist/{movieId})
    // -------------------------------------------------------------

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/scenario/watchlist.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/scenario/watchlist_cleanup.sql")
    void givenValidMovieId_whenMovieIsInWatchlist_thenMovieIsRemoved() throws Exception {
        // given
        final var movieId = 1L;

        // when
        mockMvc.perform(delete(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isNoContent());
    }

    @Test
    void givenMovieId_whenMovieIsNotInWatchlist_thenReturnMovieNotFound() throws Exception {
        // given
        final var movieId = 99L;

        // when
        mockMvc.perform(delete(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("MOVIE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Movie with ID 99 not found"));
    }

    @Test
    void givenUnknownUserEmail_whenRemovingMovieFromWatchlist_thenReturnUserNotFound() throws Exception {
        // given
        final var movieId = 99L;

        // when
        mockMvc.perform(delete(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with email %s not found".formatted(USER_EMAIL)));
    }
}
