package mydemo;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/** @author Mao DongYa create at 2022-12-23 1:59 PM */
@Slf4j
public class ContractMapTest {
  @Test
  public void contractMap() {
    Flux<String> flux1 = Flux.fromIterable(List.of("1", "2", "3"));
    Flux<String> flux2 = Flux.fromIterable(List.of("a", "b", "c"));

    flux1.concatMap(task -> flux2).subscribe(it -> log.info("concatMap={}", it));
    flux1.flatMap(task -> flux2).subscribe(it -> log.info("flatMap={}", it));
  }

  @Test
  public void delaySubscription() throws InterruptedException {
    Mono<String> mono = Mono.just("nihao").publishOn(Schedulers.boundedElastic());
    Mono<String> mono2 =
        Mono.create(
            (sink) -> {
              for (int i = 0; i < 3; i++) {
                try {
                  log.info("read...");
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
              log.info("go!!!");
              sink.success("ok");
            });
    mono2 = mono2.subscribeOn(Schedulers.boundedElastic());
    mono.delaySubscription(mono2).subscribe(it -> log.info("it={}", it));
    Thread.sleep(5000);
  }
}
