package pt.rumos.mymovies.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.service.WatchlistService;

import java.util.List;

@RestController
@RequestMapping("/api/movies/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(final WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getWatchlist(@AuthenticationPrincipal final Jwt jwt) {
        final var userEmail = jwt.getSubject();
        final var watchlist = watchlistService.getWatchlistForUser(userEmail);
        return ResponseEntity.ok(watchlist);
    }

    @PostMapping("/{id}")
    public ResponseEntity<List<MovieResponse>> addMovieToWatchlist(@PathVariable final Long id,
                                                                   @AuthenticationPrincipal final Jwt jwt) {
        final var userEmail = jwt.getSubject();
        final var watchlist = watchlistService.addMovieToWatchlist(id, userEmail);

        final var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(watchlist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMovieFromWatchlist(@PathVariable final Long id,
                                                         @AuthenticationPrincipal final Jwt jwt) {
        final var userEmail = jwt.getSubject();
        return watchlistService.removeMovieFromWatchlist(id, userEmail) ? ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
