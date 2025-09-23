package com.ai.qa.service.infrastructure.client;

import reactor.core.publisher.Mono;

public interface AIClient {
    Mono<String> askQuestion(String question);

    String getProviderName();
}
