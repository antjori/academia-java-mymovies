package pt.rumos.mymovies.config;

import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.net.InetSocketAddress;

@Configuration
public class MongoMemoryConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        final MongoServer server = new MongoServer(new MemoryBackend());
        final InetSocketAddress serverAddress = server.bind();
        final String connectionString = "mongodb://" + serverAddress.getHostName() + ":" + serverAddress.getPort() + 
                "/mymovies";
        return new MongoTemplate(new
                SimpleMongoClientDatabaseFactory(MongoClients.create(connectionString), "mymovies"));
    }
}
