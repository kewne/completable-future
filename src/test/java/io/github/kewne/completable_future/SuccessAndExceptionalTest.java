package io.github.kewne.completable_future;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SuccessAndExceptionalTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void showSuccessCallbacksNotCalledOnException() {
        getUserName()
                .whenComplete((r, t) -> {
                    throw new NullPointerException();
                })
                .thenAccept(r -> LOGGER.info("Great success with {}", r))
                .exceptionally(t -> {
                    LOGGER.info("Exception!", t);
                    return null;
                });
    }

    @Test
    public void completionMethodsCalledOnSuccess() {
        getUserName()
                .thenAccept(r -> LOGGER.info("Great success!"))
                .whenComplete((r, t) -> LOGGER.info("Still called"));
    }

    @Test
    public void completionMethodsCalledOnException() {
        getUserName()
                .whenComplete((r, t) -> {
                    throw new NullPointerException();
                })
                .thenAccept(r -> LOGGER.info("Great success!"))
                .whenComplete((r, t) -> LOGGER.info("Still called"));
    }

    @Test
    public void showExceptionCallbacksNotCalledOnSuccess() {
        getUserName()
                .thenAccept(r -> LOGGER.info("Great success with {}", r))
                .exceptionally(t -> {
                    LOGGER.info("Exception!", t);
                    return null;
                });
    }

    private CompletionStage<String> getUserName() {
        return CompletableFuture.completedFuture("kewne");
    }
}
