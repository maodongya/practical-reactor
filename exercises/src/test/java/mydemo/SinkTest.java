package mydemo;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/** @author Mao DongYa create at 2022-12-22 4:26 PM */
@Slf4j
public class SinkTest {
  public List<String> getUrls() {
    return List.of("test1", "test2", "test3");
  }

  @Test
  public void sinkTest() {
    Flux.create(
            sink -> {
              getUrls().forEach(it -> sink.next(it));
              sink.complete();
            })
        .doFirst(() -> log.info("doFirst..."))
        .doOnComplete(() -> log.info("doOnComplete..."))
        //        .publishOn(Schedulers.newBoundedElastic(1, 1, "my-elastic"))
        .publishOn(Schedulers.boundedElastic())
        .doFinally((it) -> log.info("doFinally...{}", it))
        .doAfterTerminate(() -> log.info("doAfterTerminate..."))
        .doOnNext(it -> log.info("doOnNext={}", it))
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
  }

  @Test
  public void sinkTest2() {
    Flux.create(
            sink -> {
              getUrls().forEach(it -> sink.next(it));
              sink.complete();
            })
        .doFirst(() -> log.info("doFirst..."))
        .doOnComplete(() -> log.info("doOnComplete..."))
        .publishOn(Schedulers.newBoundedElastic(1, 1, "my-elastic"))
        //                .publishOn(Schedulers.boundedElastic())
        .doFinally((it) -> log.info("doFinally...{}", it))
        .doAfterTerminate(() -> log.info("doAfterTerminate..."))
        .doOnNext(it -> log.info("doOnNext={}", it))
        .subscribeOn(Schedulers.newBoundedElastic(1, 1, "my-elastic"))
        .subscribe();
  }
}
