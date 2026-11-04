package pt.rumos.mymovies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class MyMoviesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMoviesApplication.class, args);
    }

}
