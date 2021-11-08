package com.example.demo.utility;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TaskUtility {

    // Метод нужен для того, чтобы завернуть список задач в одну задачу,
    // которая вернет список результатов выполнения списка задач
    public static <T> CompletableFuture<List<T>> allToList(List<CompletableFuture<T>> futures) {
        CompletableFuture[] completableFutures = futures.toArray(new CompletableFuture[futures.size()]);

        return CompletableFuture
                .allOf(completableFutures)
                .thenApply(ignored -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}
