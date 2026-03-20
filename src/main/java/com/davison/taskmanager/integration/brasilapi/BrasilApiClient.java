package com.davison.taskmanager.integration.brasilapi;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class BrasilApiClient {

    private final WebClient webClient;

    public BrasilApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://brasilapi.com.br/api")
                .build();
    }

    public Mono<List<HolidayDto>> getHolidays(int year) {
        return webClient.get()
                .uri("/feriados/v1/{year}", year)
                .retrieve()
                .bodyToFlux(HolidayDto.class)
                .collectList()
                // 🔥 Retry apenas para erro 5xx
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2)) // 3 tentativas com backoff
                                .filter(this::is5xxError)
                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                        new RuntimeException("Falha após tentativas de retry", retrySignal.failure())
                                )
                );
    }

    private boolean is5xxError(Throwable throwable) {
        if (throwable instanceof WebClientResponseException ex) {
            return ex.getStatusCode().is5xxServerError();
        }
        return false;
    }
}