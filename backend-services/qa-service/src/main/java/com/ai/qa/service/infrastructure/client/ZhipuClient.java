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
@Order(1)
public class ZhipuClient implements AIClient {

    private static final Logger logger = LoggerFactory.getLogger(ZhipuClient.class);

    private final WebClient webClient;

    @Value("${zhipu.api.key}")
    private String apiKey;

    @Value("${zhipu.api.url}")
    private String apiUrl;

    @Value("${zhipu.api.model}")
    private String model;

    public ZhipuClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getProviderName() {
        return "Zhipu";
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
                            logger.error("Zhipu API error: status={}, response={}", response.statusCode(),
                                    response.bodyToMono(String.class));
                            if (response.statusCode().is4xxClientError()) {
                                return response.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new RuntimeException("Zhipu API请求错误: " + body)));
                            } else {
                                return Mono.error(new RuntimeException("Zhipu API服务不可用"));
                            }
                        })
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError()))
                .map(this::extractAnswer)
                .doOnError(error -> logger.error("Failed to get answer from Zhipu API", error))
                .onErrorResume(throwable -> {
                    logger.warn("Zhipu API failed, providing fallback response for question: {}", question);
                    return Mono.just(getFallbackAnswer(question));
                });
    }

    private String extractAnswer(Map<String, Object> response) {
        try {
            if (response == null) {
                throw new RuntimeException("Empty response from Zhipu API");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices in Zhipu API response");
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
            logger.error("Error parsing Zhipu API response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Zhipu API response", e);
        }
    }

    private String getFallbackAnswer(String question) {
        // 提供简单的备用回答
        if (question.toLowerCase().contains("hello") || question.toLowerCase().contains("hi")
                || question.contains("你好")) {
            return "你好！我目前正在经历一些技术困难，我的AI服务暂时无法正常工作。我可以为您提供基本信息帮助。";
        } else if ((question.toLowerCase().contains("how") && question.toLowerCase().contains("you")) ||
                (question.contains("你") && question.contains("谁"))) {
            return "我是AI助手，但我目前连接主服务遇到问题。现在我可以提供基本回复。";
        } else {
            return "抱歉，我目前正在经历技术困难，我的AI服务暂时不可用。请稍后再试，如果问题持续存在，请联系技术支持。";
        }
    }
}
