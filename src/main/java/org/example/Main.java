package org.example;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(String[] args) {



  }

  private static void tryCompletableFuture() throws InterruptedException, ExecutionException {
    Executor executor = Executors.newFixedThreadPool(1);

    List<CompletableFuture<String>> futures = Lists.newArrayList();

    IntStream.range(0, 10).forEach(index -> {

      futures.add(CompletableFuture.supplyAsync(() -> {
        log.info("sleep {} seconds", index);
        try {
          Thread.sleep(index * 1000L);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        log.info("end sleep {} seconds", index);
        return String.valueOf(index);
      }, executor));
    });

    List<String> results = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenApply(v -> futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .collect(Collectors.toList())
        ).get();

    log.info("results: {}", results);
  }
}