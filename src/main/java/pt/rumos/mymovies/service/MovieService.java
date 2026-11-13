package pt.rumos.mymovies.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.rumos.mymovies.dto.MovieRequest;
import pt.rumos.mymovies.dto.MovieResponse;
import pt.rumos.mymovies.dto.RatingUpdateRequest;
import pt.rumos.mymovies.exception.MovieNotFoundException;
import pt.rumos.mymovies.model.Movie;
import pt.rumos.mymovies.repository.MovieRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> findAll(String title) {
        List<Movie> movies = (title != null && !title.isBlank())
                ? repository.findByTitleContainingIgnoreCase(title)
                : repository.findAll();
        return movies.stream().map(MovieResponse::fromDomain).toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse findById(Long id) {
        Movie movie = repository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        return MovieResponse.fromDomain(movie);
    }

    @Transactional
    public MovieResponse create(MovieRequest request) {
        Movie entity = new Movie(null, request.title(), request.director(), request.year());
        Movie saved = repository.save(entity);
        return MovieResponse.fromDomain(saved);
    }


    /* NOTA: Não é necessário chamar 'repository.save(movie)'!
     * Como o 'movie' é uma Entidade Gerenciada dentro de uma transação ativa (@Transactional),
     * o Hibernate faz a verificação de alterações (Dirty Checking) e executa o SQL 'UPDATE'
     * automaticamente no momento do commit.
     */
    @Transactional
    public MovieResponse update(Long id, MovieRequest request) {
        // 1. Busca a entidade existente ou lança 404 caso não exista
        Optional<Movie> optMovie = repository.findById(id);

        // 2. Atualiza o estado do objeto em memória (rating a parte)
        if (request == null || optMovie.isEmpty()) {
            return null;
        }

        Movie updatedMovie = optMovie.get();
        updatedMovie.updateData(request.title(), request.director(), request.year());
        return MovieResponse.fromDomain(updatedMovie);
    }

    @Transactional
    public Optional<MovieResponse> updateRating(Long id, RatingUpdateRequest request) {
        return repository.findById(id).map(movie -> {
            movie.setRating(request.rating());
            return MovieResponse.fromDomain(movie);
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
