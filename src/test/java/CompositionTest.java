import org.junit.Test;
import reactor.core.publisher.Flux;

public class CompositionTest {

    @Test
    public void playComposition(){

        //no transformation will be applied
        Flux<String> flux = Flux.just("foo", "chain");
        flux.map(secret -> secret.replaceAll(".", "*"));
        flux.subscribe(next -> System.out.println("Received: " + next));

        //operators wraps into new instances (immutability) -> chain composition
        Flux.just("foo", "chain")
                .map(secret -> secret.replaceAll(".", "*"))
                .subscribe(next -> System.out.println("Received: " + next));

    }
}
