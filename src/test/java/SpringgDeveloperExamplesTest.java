import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

public class SpringgDeveloperExamplesTest {

    @Test
    public void countLines() {

        List<String> words = Arrays.asList("aggsh", "hsyyss", "ksjsjsjasj", "ldjsoo", "widismmsk");
        Flux<String> flux= Flux.fromIterable(words);
        flux.zipWith(Flux.range(1, 100), (word, line) -> line + ". " + word)
            .subscribe(System.out::println);

    }

    @Test
    public void orderedLetters() {

        List<String> words = Arrays.asList("aggsh", "hsyyss", "ksjsjsjasj", "ldjsoo", "widismmsk");
        Flux<String> flux = Flux.fromIterable(words);
        flux.flatMap(word -> Flux.fromArray(word.split(""))).
                distinct().
                sort().
                zipWith(Flux.range(1, 100), (letter, line) -> line + ". " + letter)
                .subscribe(System.out::println);

    }


}
