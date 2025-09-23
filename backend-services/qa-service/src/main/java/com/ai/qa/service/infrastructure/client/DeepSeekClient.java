package com.ai.qa.service.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
public class DeepSeekClient implements AIClient {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekClient.class);

    private final WebClient webClient;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.model}")
    private String model;

    public DeepSeekClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getProviderName() {
        return "DeepSeek";
    }

    public Mono<String> askQuestion(String question) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", question)),
                "max_tokens", 1000,
                "temperature", 0.7);

        return webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            logger.error("DeepSeek API error: status={}, response={}", response.statusCode(),
                                    response.bodyToMono(String.class));
                            if (response.statusCode().is4xxClientError()) {
                                return response.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new RuntimeException("DeepSeek API请求错误: " + body)));
                            } else {
                                return Mono.error(new RuntimeException("DeepSeek API服务不可用"));
                            }
                        })
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError()))
                .map(this::extractAnswer)
                .doOnError(error -> logger.error("Failed to get answer from DeepSeek API", error));
    }

    private String extractAnswer(Map<String, Object> response) {
        try {
            if (response == null) {
                throw new RuntimeException("Empty response from DeepSeek API");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices in DeepSeek API response");
            }

            Map<String, Object> firstChoice = choices.get(0);
            if (firstChoice == null) {
                throw new RuntimeException("First choice is null");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            if (message == null) {
                throw new RuntimeException("Message is null in choice");
            }

            String content = (String) message.get("content");
            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("Content is empty in message");
            }

            return content;
        } catch (Exception e) {
            logger.error("Error parsing DeepSeek API response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse DeepSeek API response", e);
        }
    }
}
