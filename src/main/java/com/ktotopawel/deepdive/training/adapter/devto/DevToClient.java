package com.ktotopawel.deepdive.training.adapter.devto;

import com.ktotopawel.deepdive.training.domain.model.Label;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Component
public class DevToClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://dev.to/api/")
            .build();

    public List<DevToArticleDTO> fetchAll() {
        Set<Long> articleIds = new HashSet<>();
        for (Label label : Label.values()) {
            List<FetchByLabelDevToDTO> rawFetch = fetchByLabel(label.getTag());
            articleIds.addAll(rawFetch.stream().map(FetchByLabelDevToDTO::id).toList());
        }
        return fetchArticlesConcurrently(articleIds, 6);
    }

    private List<FetchByLabelDevToDTO> fetchByLabel(String label) {
        List<FetchByLabelDevToDTO> articles = new ArrayList<>();
        int curPage = 1;

        while (true) {
            try {
                int finalCurPage = curPage;
                List<FetchByLabelDevToDTO> page = retryOn429(
                        () ->
                                webClient.get()
                                        .uri(uriBuilder -> uriBuilder
                                                .path("articles")
                                                .queryParam("tag", label)
                                                .queryParam("per_page", 1000)
                                                .queryParam("page", finalCurPage)
                                                .build()
                                        )
                                        .header("User-Agent", "deepdive-training-loader/1.0")
                                        .retrieve()
                                        .bodyToFlux(FetchByLabelDevToDTO.class)
                                        .collectList()
                                        .block(Duration.ofSeconds(10)),
                        10,
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(10)
                );

                if (page == null || page.isEmpty()) {
                    break;
                }

                articles.addAll(page);
                curPage++;
            } catch (org.springframework.web.reactive.function.client.WebClientResponseException ex) {
                int status = ex.getStatusCode().value();
                String body = ex.getResponseBodyAsString();

                System.out.println("DEV.to returned HTTP " + status + " body=" + body);

                throw ex;
            }
        }

        return articles;
    }

    private List<DevToArticleDTO> fetchArticlesConcurrently(Collection<Long> ids, int concurrentCalls) {
        try (ExecutorService pool = Executors.newFixedThreadPool(concurrentCalls)) {
            List<CompletableFuture<DevToArticleDTO>> futures = ids.stream()
                    .map(id -> CompletableFuture.supplyAsync(() ->
                            fetchArticleById(id), pool))
                    .toList();
            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();
        }
    }

    private DevToArticleDTO fetchArticleById(Long id) {

        try {
            return retryOn429(() -> webClient.get()
                            .uri("articles/" + id)
                            .header("User-Agent", "deepdive-training-loader/1.0")
                            .retrieve()
                            .bodyToMono(DevToArticleDTO.class)
                            .block(Duration.ofSeconds(10)),
                    10,
                    Duration.ofSeconds(1),
                    Duration.ofSeconds(10)
            );
        } catch (WebClientResponseException ex) {
            int status = ex.getStatusCode().value();
            String body = ex.getResponseBodyAsString();
            System.out.println("DEV.to returned HTTP " + status + " body=" + body);

            if (status == 429) {
                sleepSilently(2000L);
                return fetchArticleById(id);
            }

            throw ex;
        }
    }

    private <T> T retryOn429(Supplier<T> call, int maxRetries, Duration baseBackoff, Duration maxBackoff) {
        int attempt = 0;

        while (true) {
            try {
                return call.get();
            } catch (WebClientResponseException ex) {
                int status = ex.getStatusCode().value();

                if (status != 429 || attempt >= maxRetries) {
                    throw ex;
                }

                long backoffMs = computeBackoffMs(baseBackoff, maxBackoff, attempt);
                System.out.println("429 rate-limited; retry " + (attempt + 1) + "/" + maxRetries + " sleeping " + backoffMs + "ms");

                sleepSilently(backoffMs);
                attempt++;
            }
        }
    }

    private long computeBackoffMs(Duration base, Duration max, int attempt) {
        long exp = (long) (base.toMillis() * Math.pow(2, attempt));
        long capped = Math.min(exp, max.toMillis());
        long jitter = ThreadLocalRandom.current().nextLong(0, 251);
        return capped + jitter;
    }

    private void sleepSilently(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
