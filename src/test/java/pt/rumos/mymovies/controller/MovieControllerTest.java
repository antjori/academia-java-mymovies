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

import pt.rumos.mymovies.dto.MovieRequest;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.dto.RatingUpdateRequest;
import pt.rumos.mymovies.service.MovieService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private MovieResponse movieResponse;

    private MovieRequest movieRequest;

    private RatingUpdateRequest ratingRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(movieController)
                .build();

        // Adjust these constructors to match your DTOs.
        movieRequest = new MovieRequest(
                "The Matrix",
                "The Brothers",
                1999
        );

        movieResponse = new MovieResponse(
                1L,
                "The Matrix",
                "The Brothers",
                1999,
                9d
        );

        ratingRequest = new RatingUpdateRequest(9.0);
    }

    // --------------------------------------------------
    // GET /api/movies
    // --------------------------------------------------

    @Test
    void shouldReturnAllMovies() throws Exception {

        when(movieService.findAll(null))
                .thenReturn(List.of(movieResponse));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("The Matrix"));

        verify(movieService).findAll(null);
    }

    @Test
    void shouldReturnMoviesFilteredByTitle() throws Exception {

        when(movieService.findAll("matrix"))
                .thenReturn(List.of(movieResponse));

        mockMvc.perform(get("/api/movies")
                        .param("title", "matrix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("The Matrix"));

        verify(movieService).findAll("matrix");
    }

    @Test
    void shouldReturnEmptyListWhenNoMoviesExist() throws Exception {

        when(movieService.findAll(null))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --------------------------------------------------
    // GET /api/movies/{id}
    // --------------------------------------------------

    @Test
    void shouldReturnMovieById() throws Exception {

        when(movieService.findById(1L))
                .thenReturn(movieResponse);

        mockMvc.perform(get("/api/movies/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Matrix"));

        verify(movieService).findById(1L);
    }

    // --------------------------------------------------
    // POST /api/movies
    // --------------------------------------------------

    @Test
    void shouldCreateMovie() throws Exception {

        when(movieService.create(any(MovieRequest.class)))
                .thenReturn(movieResponse);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", "http://localhost/api/movies/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Matrix"));

        verify(movieService).create(any(MovieRequest.class));
    }

    // --------------------------------------------------
    // PUT /api/movies/{id}
    // --------------------------------------------------

    @Test
    void shouldUpdateMovie() throws Exception {

        when(movieService.update(eq(1L), any(MovieRequest.class)))
                .thenReturn(movieResponse);

        mockMvc.perform(put("/api/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Matrix"));

        verify(movieService).update(eq(1L), any(MovieRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingMovie()
            throws Exception {

        when(movieService.update(eq(99L), any(MovieRequest.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/movies/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequest)))
                .andExpect(status().isNotFound());

        verify(movieService).update(eq(99L), any(MovieRequest.class));
    }

    // --------------------------------------------------
    // DELETE /api/movies/{id}
    // --------------------------------------------------

    @Test
    void shouldDeleteMovie() throws Exception {

        when(movieService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/movies/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(movieService).delete(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingMovie()
            throws Exception {

        when(movieService.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/movies/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(movieService).delete(99L);
    }

    // --------------------------------------------------
    // PATCH /api/movies/{id}/rating
    // --------------------------------------------------

    @Test
    void shouldUpdateMovieRating() throws Exception {

        when(movieService.updateRating(
                eq(1L), any(RatingUpdateRequest.class)))
                .thenReturn(Optional.of(movieResponse));

        mockMvc.perform(patch("/api/movies/{id}/rating", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Matrix"));

        verify(movieService).updateRating(
                eq(1L), any(RatingUpdateRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingRatingOfNonExistingMovie()
            throws Exception {

        when(movieService.updateRating(
                eq(99L), any(RatingUpdateRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/movies/{id}/rating", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequest)))
                .andExpect(status().isNotFound());

        verify(movieService).updateRating(
                eq(99L), any(RatingUpdateRequest.class));
    }
}
