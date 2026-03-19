package com.davison.taskmanager.integration.brasilapi;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BrasilApiClient {

    private final WebClient webClient;

    public BrasilApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://brasilapi.com.br/api").build();
    }

    public Mono<List<HolidayDto>> getHolidays(int year) {
        return webClient.get()
                .uri("/feriados/v1/{year}", year)
                .retrieve()
                .bodyToFlux(HolidayDto.class)
                .collectList();
    }
}