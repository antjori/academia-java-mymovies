package pt.rumos.mymovies.controller;

import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pt.rumos.mymovies.model.Movie;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private static final List<Movie> MOVIES = List.of(
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
            new Movie(21L, "Avatar", "James Cameron", 2009),
            new Movie(22L, "The Shawshank Redemption", "Frank Darabont", 1994)
    );


    @GetMapping
    public List<Movie> listAll() {
        return MOVIES;
    }

    @GetMapping("/{id}")
    public Movie getById(@PathVariable final Long id) {
        return MOVIES.stream()
                .filter(movie -> movie.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/search")
    public List<Movie> searchByTitle(@RequestParam(required = false) final String title) {
        if (StringUtils.isBlank(title)) {
            return MOVIES;
        }

        return MOVIES.stream().filter(movie -> movie.title().toLowerCase().contains(title.toLowerCase())).toList();
    }
}
