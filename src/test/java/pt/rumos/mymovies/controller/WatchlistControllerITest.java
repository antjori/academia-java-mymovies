package pt.rumos.mymovies.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for {@link WatchlistController}.
 * <p>
 * It aims at testing all watchlist endpoints, for happy paths and error scenarios.
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/scenario/user.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS, scripts = "/scenario/user_cleanup.sql")
class WatchlistControllerITest {

    private static final String BASE_URL = "/api/movies";

    private static final String WATCHLIST_URL = BASE_URL + "/watchlist";

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestSecurityConfig {

        private static final String AUTH0_TOKEN = "token";

        private static final String USER_EMAIL = "test@example.com";

        @Bean
        public JwtDecoder jwtDecoder() {
            return token -> jwt();
        }

        public Jwt jwt() {
            Map<String, Object> claims = Map.of(
                    JwtClaimNames.SUB, USER_EMAIL
            );

            return new Jwt(
                    AUTH0_TOKEN,
                    Instant.now(),
                    Instant.now().plusSeconds(30),
                    Map.of("alg", "none"),
                    claims
            );
        }
    }

    // ----------------------------------
    // Get Watchlist (GET /api/watchlist)
    // ----------------------------------

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/scenario/watchlist_cleanup.sql")
    void givenValidRequest_whenWatchlistIsEmpty_thenReturnEmptyList() throws Exception {
        // given

        // when
        mockMvc.perform(get(WATCHLIST_URL).header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                // .with(SecurityMockMvcRequestPostProcessors.jwt()))
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
        mockMvc.perform(get(WATCHLIST_URL).header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void givenNoJwt_whenRetrievingWatchlist_thenUnauthorizedIsReturned() throws Exception {
        // given

        // when
        mockMvc.perform(get(WATCHLIST_URL))
                // then
                .andExpect(status().isUnauthorized());
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
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
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
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                // then
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("MOVIE_ALREADY_IN_WATCHLIST"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Movie with ID 1 already in watchlist"));
    }

    @Test
    void givenUnknownMovieId_whenAddingMovieToWatchlist_thenReturnMovieNotFound() throws Exception {
        // given
        final var movieId = 99L;

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("MOVIE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Movie not found: %d".formatted(movieId)));
    }

    @Test
    void givenUnknownUser_whenAddingMovieToWatchlist_thenReturnUserNotFound() throws Exception {
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
                .andExpect(jsonPath("$.message").value("User with email user not found"));
    }

    @Test
    void givenNoJwt_whenAddingMovieToWatchlist_thenUnauthorizedIsReturned() throws Exception {
        // given
        final var movieId = 1L;

        // when
        mockMvc.perform(post(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized());
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
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                // then
                .andExpect(status().isNoContent());

        mockMvc.perform(get(WATCHLIST_URL).header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                // .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void givenMovieId_whenMovieIsNotInWatchlist_thenReturnMovieNotFound() throws Exception {
        // given
        final var movieId = 99L;

        // when
        mockMvc.perform(delete(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MOVIE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Movie not found: 99"));
    }

    @Test
        // @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/scenario/user_cleanup.sql")
    void givenUnknownUser_whenRemovingMovieFromWatchlist_thenReturnUserNotFound() throws Exception {
        // given
        final var movieId = 99L;

        // when
        mockMvc.perform(delete(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with email user not found"));
    }

    @Test
    void givenNoJwt_whenRemovingMovieFromWatchlist_thenUnauthorizedIsReturned() throws Exception {
        // given
        final var movieId = 1L;

        // when
        mockMvc.perform(delete(WATCHLIST_URL + "/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized());
    }
}
