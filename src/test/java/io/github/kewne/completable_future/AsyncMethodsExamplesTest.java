package io.github.kewne.completable_future;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class AsyncMethodsExamplesTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncMethodsExamplesTest.class);

    @Test
    public void testSingleStage() {
        LOGGER.info("Starting test...");
        CompletableFuture<String> future = new CompletableFuture<>();
        CompletableFuture<Void> result = future.thenAcceptAsync(r -> LOGGER.info("Accepting result {}", r));
        future.complete("hello");
        result.join();
    }

    @Test
    public void testMultipleStage() {
        LOGGER.info("Starting test...");
        CompletableFuture<String> future = new CompletableFuture<>();
        CompletableFuture<Void> result = future.thenApplyAsync(r -> {
            LOGGER.info("Mapping result {}", r);
            return r.toUpperCase();
        }).thenAcceptAsync(r -> LOGGER.info("Accepting result {}", r));
        future.complete("hello");
        result.join();
    }

    @Test
    public void testDifferentThreadCompletion() {
        LOGGER.info("Starting test...");
        CompletableFuture<String> future = new CompletableFuture<>();
        CompletableFuture<Void> result = future.thenApplyAsync(r -> {
            LOGGER.info("Mapping result {}", r);
            return r.toUpperCase();
        }).thenAcceptAsync(r -> LOGGER.info("Accepting result {}", r));
        new Thread("other-Thread") {
            @Override
            public void run() {
                LOGGER.info("Completing future...");
                future.complete("hello");
            }
        }.start();
        result.join();
    }
}
