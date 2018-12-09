import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class SubscriptionTest {

    @Test
    public void playSubscriptions(){

        //just triggers the flux
        Flux<Integer> ints = Flux.range(1, 3);
        ints.subscribe();

        //Triggers the flux and process the result
        ints.subscribe(i -> System.out.println(i));
        System.out.println();

        //Triggers the flux, process the result and react to errors
        ints.map(i -> {
            if (i <= 3) return i;
            throw new RuntimeException("Got to 4");
        });
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error: " + error));
        System.out.println();

        //Triggers the flux, process the result, react to errors and to completion
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"));
        System.out.println();

        //Triggers the flux, process the result, react to errors and to completion, and
        //cancel or bound the number of events triggered by the Flux.
        // Here we will get an error, as the Flux has length 4.
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"),
                sub -> sub.request(2));
    }

    @Test
    public void delayedFlux() throws Exception{

        Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .subscribe(i -> System.out.println(i));
        Thread.sleep(11000);

    }
}
