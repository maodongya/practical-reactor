package mydemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;

import java.util.stream.IntStream;

public class FluxTest2 {
  @Test
  void testCreateToWrapMultiThreadsAsyncExternalAPI() {
    SequenceCreator sequenceCreator = new SequenceCreator();
    int numberOfElements = 10000;

    StepVerifier.create(sequenceCreator.createNumberSequence(numberOfElements))
        .expectNextCount(numberOfElements)
        .verifyComplete();
  }

  class SequenceCreator {

    public Flux<Integer> createNumberSequence(Integer elementsToEmit) {
      return Flux.create(sharedSink -> multiThreadSource(elementsToEmit, sharedSink));
    }

    void multiThreadSource(Integer elementsToEmit, FluxSink<Integer> sharedSink) {
      Thread producingThread1 =
          new Thread(() -> emitElements(sharedSink, elementsToEmit / 2), "Thread_1");
      Thread producingThread2 =
          new Thread(() -> emitElements(sharedSink, elementsToEmit / 2), "Thread_2");

      producingThread1.start(); // Start to emit elements
      producingThread2.start();

      try {
        producingThread1.join(); // Wait that thread finishes
        producingThread2.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      sharedSink.complete();
    }

    public void emitElements(FluxSink<Integer> sink, Integer count) {
      IntStream.range(1, count + 1)
          .boxed()
          .forEach(
              n -> {
                System.out.printf("onNext {}", n);
                sink.next(n);
              });
    }
  }
}
