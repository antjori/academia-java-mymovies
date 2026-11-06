package pt.rumos.mymovies.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pt.rumos.mymovies.dto.tmdb.TmdbMovie;
import pt.rumos.mymovies.dto.tmdb.TmdbSearchResponse;

@Component
public class TmdbClient {

    private final RestClient client;

    public TmdbClient(final RestClient.Builder builder,
                      @Value("${tmdb.base-url}") final String baseUrl,
                      @Value("${tmdb.token}") final String token) {
        this.client = builder.baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    public TmdbSearchResponse searchMovies(final String query) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/search/movie")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .body(TmdbSearchResponse.class);
    }

    public TmdbMovie getMovieDetails(final long tmdbId) {
        return client.get()
                .uri("/movie/{id}", tmdbId)
                .retrieve()
                .body(TmdbMovie.class);
    }
}
