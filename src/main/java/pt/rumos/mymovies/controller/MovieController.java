package pt.rumos.mymovies.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Endpoint 1: Busca todos os filmes ou por parte do título (case-insensitive).
     * HTTP GET /api/movies?title=matrix
     */
    @GetMapping
    public ResponseEntity<List<MovieResponse>> list(@RequestParam(required = false) String title) {
        List<MovieResponse> response = movieService.findAll(title);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 2: Busca um filme individual pelo seu ID.
     * HTTP GET /api/movies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getById(@PathVariable Long id) {
        var movieResponse = movieService.findById(id);
        return ResponseEntity.ok(movieResponse);
    }


    /**
     * Endpoint 4: Cria um filme.
     * HTTP POST /api/movies
     */
    @PostMapping
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody MovieRequest request) {
        MovieResponse created = movieService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * Endpoint 5: Altera dados de um file existente
     * HTTP PUT /api/movies/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody MovieRequest request
    ) {
        MovieResponse updatedMovie = movieService.update(id, request);
        if(updatedMovie == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedMovie);
    }

    /**
     * Endpoint 6: Remove um filme
     * HTTP DELETE /api/movies/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = movieService.delete(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<MovieResponse> updateRating(@PathVariable Long id, @Valid @RequestBody RatingUpdateRequest request) {
        var optionalMovieResponse = movieService.updateRating(id,request);

        return optionalMovieResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
