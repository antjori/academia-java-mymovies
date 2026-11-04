package pt.rumos.mymovies.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.rumos.mymovies.dto.MovieRequest;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.dto.RatingUpdateRequest;
import pt.rumos.mymovies.service.MovieService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(final MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Endpoint 1: Retorna todos os filmes cadastrados.
     * HTTP GET /api/movies
     */
    @GetMapping
    public ResponseEntity<List<MovieResponse>> listAll() {
        final var movies = movieService.findAll();
        return ResponseEntity.ok(movies);
    }


    /**
     * Endpoint 2: Busca um filme individual pelo seu ID.
     * HTTP GET /api/movies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getById(@PathVariable String id) {
        return movieService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint 3: Busca filmes por parte do título (case-insensitive).
     * HTTP GET /api/movies/search?title=matrix
     */
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchByTitle(@RequestParam(required = false) String title) {
        final var movies = StringUtils.hasText(title) ? movieService.findByTitleContainingIgnoreCase(title.trim()) :
                movieService.findAll();

        return ResponseEntity.ok(movies);
    }

    @PostMapping
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody MovieRequest request) {
        final var newMovie = movieService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newMovie.id())
                .toUri();

        return ResponseEntity.created(location).body(newMovie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> update(@PathVariable String id,
                                                @Valid @RequestBody MovieRequest request
    ) {
        return movieService.update(id, request).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return movieService.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<MovieResponse> updateRating(@PathVariable String id,
                                                      @Valid @RequestBody RatingUpdateRequest request) {
        return movieService.updateRating(id, request).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
