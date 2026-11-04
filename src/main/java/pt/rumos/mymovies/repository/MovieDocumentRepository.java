package pt.rumos.mymovies.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pt.rumos.mymovies.model.MovieDocument;

import java.util.List;

public interface MovieDocumentRepository extends MongoRepository<MovieDocument, String> {

    List<MovieDocument> findByTitleContainingIgnoreCase(String title);
}
