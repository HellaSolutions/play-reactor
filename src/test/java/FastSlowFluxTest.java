import org.junit.After;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;

public class FastSlowFluxTest {

    @Test
    public void mergeFlux(){

        Flux<Long> evenFlux = Flux.interval(Duration.ofMillis(200)).map(tick -> 2*tick);
        Flux<Long> oddFlux = Flux.interval(Duration.ofMillis(400)).map(tick -> 2*tick + 1);
        Flux<LocalTime> clock = Flux.interval(Duration.ofMillis(200)).map(tick -> LocalTime.now());
        Flux merged = Flux.merge(evenFlux, oddFlux).zipWith(clock, (n, time) -> n + " " + time);
        merged.subscribe(System.out::println);

    }

    @After
    public void after() throws Exception{
        Thread.sleep(10000);
    }
}
