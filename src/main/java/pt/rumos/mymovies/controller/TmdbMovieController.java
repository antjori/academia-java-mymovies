package pt.rumos.mymovies.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.rumos.mymovies.dto.MovieCandidateResponse;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.service.TmdbService;

import java.util.List;

@RestController
@RequestMapping("/api/tmdb/movies")
public class TmdbMovieController {

    private final TmdbService tmdbService;

    public TmdbMovieController(final TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieCandidateResponse>> search(@RequestParam final String query) {
        return ResponseEntity.ok(tmdbService.searchCandidates(query));
    }

    @PostMapping("/{tmdbId}/import")
    public ResponseEntity<MovieResponse> importMovie(@PathVariable final long tmdbId) {
        MovieResponse imported = tmdbService.importMovie(tmdbId);
        return ResponseEntity.status(HttpStatus.CREATED).body(imported);
    }
}
