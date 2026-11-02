package pt.rumos.mymovies.model;

public record Movie(
        Long id,
        String title,
        String director,
        Integer year,
        Double rating
) {
    public Movie(Long id,
                 String title,
                 String director,
                 Integer year) {
        this(id, title, director, year, 0.0);
    }
}
