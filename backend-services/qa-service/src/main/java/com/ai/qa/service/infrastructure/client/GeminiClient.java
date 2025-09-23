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
import java.util.Map;

@Component
@Order(1)
public class GeminiClient implements AIClient {

    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getProviderName() {
        return "Gemini";
    }

    public Mono<String> askQuestion(String question) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", question)
                        })
                });

        return webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            logger.error("Gemini API error: status={}, response={}", response.statusCode(),
                                    response.bodyToMono(String.class));
                            if (response.statusCode().is4xxClientError()) {
                                return response.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new RuntimeException("Gemini API请求错误: " + body)));
                            } else {
                                return Mono.error(new RuntimeException("Gemini API服务不可用"));
                            }
                        })
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError()))
                .map(this::extractAnswer)
                .doOnError(error -> logger.error("Failed to get answer from Gemini API", error))
                .onErrorResume(throwable -> {
                    logger.warn("Gemini API failed, providing fallback response for question: {}", question);
                    return Mono.just(getFallbackAnswer(question));
                });
    }

    private String extractAnswer(Map<String, Object> response) {
        try {
            if (response == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> candidates = (java.util.List<Map<String, Object>>) response
                    .get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates in Gemini API response");
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            if (firstCandidate == null) {
                throw new RuntimeException("First candidate is null");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            if (content == null) {
                throw new RuntimeException("Content is null in candidate");
            }

            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> parts = (java.util.List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("No parts in content");
            }

            Map<String, Object> part = parts.get(0);
            if (part == null) {
                throw new RuntimeException("First part is null");
            }

            String text = (String) part.get("text");
            if (text == null || text.trim().isEmpty()) {
                throw new RuntimeException("Text is empty in part");
            }

            return text;
        } catch (Exception e) {
            logger.error("Error parsing Gemini API response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Gemini API response", e);
        }
    }

    private String getFallbackAnswer(String question) {
        // 提供简单的备用回答
        if (question.toLowerCase().contains("hello") || question.toLowerCase().contains("hi")) {
            return "Hello! I'm currently experiencing some technical difficulties with my AI service. How can I help you with basic information?";
        } else if (question.toLowerCase().contains("how") && question.toLowerCase().contains("you")) {
            return "I'm an AI assistant, but I'm currently having trouble connecting to my main service. I can provide basic responses for now.";
        } else {
            return "I'm sorry, but I'm currently experiencing technical difficulties with my AI service. Please try again later or contact support if the issue persists.";
        }
    }
}
