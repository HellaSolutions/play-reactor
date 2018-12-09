import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class PlayWith{

    public static void main(String[] args) throws Exception{

        EventProcessor e = new EventProcessor(10, 10);
        e.register(new EventProcessor.MyEventListener(){

            @Override
            public void onDataChunk(List<String> chunk) {
                System.out.println("received ->" + chunk.get(0));
            }

            @Override
            public void processComplete() {
                System.out.println("complete");
            }
        });
        e.start();

    }

}
