package pt.rumos.mymovies.dto;

public record MovieCandidateResponse(
        long tmdbId,
        String title,
        String releaseDate) {

}
