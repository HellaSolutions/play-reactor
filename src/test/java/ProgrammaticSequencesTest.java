import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ProgrammaticSequencesTest {

    /*
     * This is for synchronous and one-by-one emissions
     * Uses java.util.concurrent functional interfaces
     * First argument: Callable<S> : initial state
     * Second argument (optional):  BiFunction<S, SynchronousSink<T>, S>: given a state and a sink returns a state
     * Third argument (optional): Consumer<? super S> stateConsumer
    */
    @Test
    public void synchronousGenerate() {

        Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3*state);
                    if (state == 10) sink.complete();
                    return state + 1;
                }).subscribe(m -> System.out.println(m));

       Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3*i);
                    if (i == 10) sink.complete();
                    return state;
                }, (state) -> System.out.println("state: " + state)).subscribe();

    }

    /*
    * First Argument: Consumer<? super FluxSink<T>> emitter
    * Second argument:  FluxSink.OverflowStrategy
    *
    * Variant: push -> sink methods called by a single thread at time
    */
    @Test
    public void asynchronousMultiThreadCreate() throws Exception {

        EventProcessor ep = new EventProcessor(10, 10);
        Flux<String> bridge = Flux.create(sink -> {
            ep.register(
                    new EventProcessor.MyEventListener() {

                        public void onDataChunk(List<String> chunk) {
                            System.out.println(Thread.currentThread().getId());
                            for(String s : chunk) {
                                sink.next(s);
                            }
                        }

                        public void processComplete() {
                            sink.complete();
                        }
                    });
        });
        bridge.delayElements(Duration.ofMillis(0)).subscribe(System.out::println,
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"),
                sub -> sub.request(100));
        ep.start();
        Thread.sleep(3000);

    }

    /*
     * Handle the items emitted by this Flux by calling a biconsumer with the output sink for each onNext.
     *
     * Flux<R> handle(BiConsumer<T, SynchronousSink<R>>);
     *
     * It is close to generate, in the sense that it uses a SynchronousSink and only allows one-by-one emissions.
     * However, handle can be used to generate an arbitrary value out of each source element,
     * possibly skipping some elements. In this way, it can serve as a combination of map and filter. The signature of handle is as follows
     */
    @Test
    public void synchronousMultiThreadHandle() throws Exception {

        Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
                .handle((i, sink) -> {
                    String letter = alphabet(i);
                    if (letter != null)
                        sink.next(letter);
                });

        alphabet.subscribe(System.out::println);

    }

    public String alphabet(int letterNumber) {
        if (letterNumber < 1 || letterNumber > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letterNumber - 1;
        return "" + (char) letterIndexAscii;
    }

}
