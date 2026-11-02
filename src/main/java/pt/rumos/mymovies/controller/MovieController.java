package pt.rumos.mymovies.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.rumos.mymovies.dto.MovieRequest;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.dto.RatingUpdateRequest;
import pt.rumos.mymovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    // Lista fixa em memória com 22 filmes para testes e validação
    private final List<Movie> movies = new ArrayList<>(List.of(
            new Movie(1L, "Interstellar", "Christopher Nolan", 2014),
            new Movie(2L, "The Matrix", "Lana & Lilly Wachowski", 1999),
            new Movie(3L, "Inception", "Christopher Nolan", 2010),
            new Movie(4L, "The Dark Knight", "Christopher Nolan", 2008),
            new Movie(5L, "Pulp Fiction", "Quentin Tarantino", 1994),
            new Movie(6L, "The Godfather", "Francis Ford Coppola", 1972),
            new Movie(7L, "Fight Club", "David Fincher", 1999),
            new Movie(8L, "Forrest Gump", "Robert Zemeckis", 1994),
            new Movie(9L, "The Lord of the Rings: The Fellowship of the Ring", "Peter Jackson", 2001),
            new Movie(10L, "Star Wars: Episode V - The Empire Strikes Back", "Irvin Kershner", 1980),
            new Movie(11L, "The Matrix Reloaded", "Lana & Lilly Wachowski", 2003),
            new Movie(12L, "The Matrix Revolutions", "Lana & Lilly Wachowski", 2003),
            new Movie(13L, "Gladiator", "Ridley Scott", 2000),
            new Movie(14L, "Back to the Future", "Robert Zemeckis", 1985),
            new Movie(15L, "Jurassic Park", "Steven Spielberg", 1993),
            new Movie(16L, "Alien", "Ridley Scott", 1979),
            new Movie(17L, "Blade Runner 2049", "Denis Villeneuve", 2017),
            new Movie(18L, "Dune", "Denis Villeneuve", 2021),
            new Movie(19L, "Dune: Part Two", "Denis Villeneuve", 2024),
            new Movie(20L, "Oppenheimer", "Christopher Nolan", 2023),
            new Movie(21L, "Avatar", "James Cameron", 2009)
    ));

    /**
     * Endpoint 1: Retorna todos os filmes cadastrados.
     * HTTP GET /api/movies
     */
    @GetMapping
    public ResponseEntity<List<MovieResponse>> listAll() {
        return ResponseEntity.ok(movies.stream().map(MovieResponse::fromDomain).toList());
    }

    /**
     * Endpoint 2: Busca um filme individual pelo seu ID.
     * HTTP GET /api/movies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getById(@PathVariable final Long id) {
        return movies.stream()
                .filter(m -> m.id().equals(id))
                .findFirst()
                .map(MovieResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint 3: Busca filmes por parte do título (case-insensitive).
     * HTTP GET /api/movies/search?title=matrix
     */
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchByTitle(@RequestParam(required = false) final String title) {
        if (title == null || title.isBlank()) {
            return ResponseEntity.ok(movies.stream().map(MovieResponse::fromDomain).toList());
        }

        final var term = title.trim().toLowerCase();

        return ResponseEntity.ok(movies.stream()
                .filter(m -> m.title().toLowerCase().contains(term))
                .map(MovieResponse::fromDomain)
                .toList());
    }

    @PostMapping
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody final MovieRequest movieRequest) {
        final var id = movies.getLast().id() + 1;
        final var movie = new Movie(id, movieRequest.title(), movieRequest.director(), movieRequest.year());
        movies.add(movie);

        final var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).body(MovieResponse.fromDomain(movie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> update(@PathVariable final Long id,
                                                @Valid @RequestBody final MovieRequest movieRequest) {
        return movies.stream()
                .filter(m -> m.id().equals(id))
                .findFirst()
                .map(movie -> {
                    final var updatedMovie = new Movie(movie.id(), movieRequest.title(), movieRequest.director(), movieRequest.year());
                    // movies.remove(movie);
                    //movies.add(updatedMovie);
                    final var updatedMovieIndex = movies.indexOf(movie);
                    movies.set(updatedMovieIndex, updatedMovie);
                    return updatedMovie;
                })
                .map(MovieResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        /*return movies.stream()
                .filter(m -> m.id().equals(id))
                .findFirst()
                .map(movie -> {
                    movies.remove(movie);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());*/

        final var removed = movies.removeIf(m -> m.id().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<MovieResponse> updateRating(@PathVariable final Long id,
                                                      @Valid @RequestBody final RatingUpdateRequest ratingUpdateRequest) {
        return movies.stream().filter(m -> m.id().equals(id))
                .findFirst()
                .map(movie -> {
                    final var updatedMovie = new Movie(movie.id(), movie.title(), movie.director(), movie.year(), ratingUpdateRequest.rating());
                    final var updatedMovieIndex = movies.indexOf(movie);
                    movies.set(updatedMovieIndex, updatedMovie);
                    return updatedMovie;
                })
                .map(MovieResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
