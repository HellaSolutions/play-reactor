import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class EventProcessor {

    private static final int MAX_DELAY = 50;

    private int numSignals = 0;
    private int numThreads = 0;

    private List<String> history = new ArrayList<>();

    public interface MyEventListener {
        void onDataChunk(List<String> chunk);
        void processComplete();
    }

    private MyEventListener listener;

    public EventProcessor(int numThreads, int numSignals){
        this.numThreads = numThreads;
        this.numSignals = numSignals;
    }

    public CompletableFuture<String> createCompletableFuture(int futureIndex){

        return CompletableFuture.supplyAsync(() -> {
            Random r = new Random();
            int delay = r.nextInt(MAX_DELAY);
            for (int signalIndex = 0; signalIndex <= numSignals - 1; signalIndex++){

                try {
                    Thread.sleep(delay);
                } catch (Exception e) {}
                this.signal(String.format("Thread %d signal %d", futureIndex, signalIndex));
            }
            return "ok";
        });

    }

    public void start() throws Exception{
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int futureIndex = 0; futureIndex <= numThreads - 1; futureIndex++){
            futures.add(createCompletableFuture(futureIndex));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenAcceptAsync(c -> {
                    if (listener != null){
                        listener.processComplete();
                    }
                }).get();

    }

    public void signal(String message){
        history.add(message);
        if (listener != null){
            listener.onDataChunk(Arrays.asList(message));
        }
    }



    public void register(MyEventListener listener){
        this.listener = listener;
    }


}
