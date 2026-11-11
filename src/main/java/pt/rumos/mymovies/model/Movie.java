package pt.rumos.mymovies.model;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false, length = 120)
    private String title;
    @Column(name = "director", nullable = false)
    private String director;
    @Column(name = "release_year", nullable = false)
    private Integer year;
    @Column(name = "rating")
    private Double rating;

    public Movie(){}

    public Movie(Long id, String title, String director, Integer year, Double rating) {
        this.setId(id);
        this.setTitle(title);
        this.setDirector(director);
        this.setYear(year);
        this.setRating(rating);
    }

    public Movie(Long id, String title, String director, Integer year) {
        this(id, title, director, year, null);
    }

    // Método utilitário para atualização (Encapsulamento de regras)
    public void updateData(String title, String director, Integer year) {
        this.setTitle(title);
        this.setDirector(director);
        this.setYear(year);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
