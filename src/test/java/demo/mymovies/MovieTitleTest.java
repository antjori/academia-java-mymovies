package demo.mymovies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MovieTitleTest {

    @Test
    void removesEdgeSpaces() {
        var rule = new MovieTitle();
        var actual = rule.clean("  Alien  ");
        assertEquals("Alien", actual);
    }
}
